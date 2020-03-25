/*
 * Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.swiftleap.update.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.swiftleap.common.config.PropKeys;
import org.swiftleap.common.util.IOUtil;
import org.swiftleap.common.util.MD5Util;
import org.swiftleap.common.util.StringUtil;
import org.swiftleap.update.*;
import org.swiftleap.update.impl.model.NodeInfoDbo;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class UpdateServiceImpl implements UpdateService {
    private static final Logger log = LoggerFactory.getLogger(UpdateServiceImpl.class);
    private final Path updateDir = Paths.get("data", "update");
    private final Timer timer = new Timer();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Versioned zeroVersion = new Version(0, 0, 0, "Initial", "Initial");
    @Value(value = PropKeys._APP_NAME)
    String appName;
    @Value(value = PropKeys._NODE_NAME)
    String nodeName;
    @Value(value = PropKeys._UPDATE_NODE_INFO_URL)
    String updateInfoBaseUrl;
    @Autowired(required = false)
    VersionProvider versionProv;
    @Value(value = PropKeys._UPDATE_REPO_URL)
    String updateBaseUrl;
    private Catalog catalog = new Catalog();
    private Versioned patched = null;
    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        log.info("Config: Node Name: " + nodeName);
        log.info("Config: App Name: " + appName);
        log.info("Config: Update URL: " + updateBaseUrl);
        log.info("Config: Node Tracking URL: " + updateInfoBaseUrl);

        updateDir.toFile().mkdirs();
        loadCatalog();
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          timerTick();
                                      }
                                  },
                60000, 1800000);
    }

    private void loadCatalog() {
        File catFile = updateDir.resolve("catalog.json").toFile();
        if (!catFile.exists())
            return;
        try {
            catalog = mapper.readValue(catFile, Catalog.class);
        } catch (IOException ex) {
            catFile.delete();
            log.error("Failed to load catalog: " + ex.getMessage());
        }
    }

    private void timerTick() {
        //sendNodeInfo();
        try {
            downloadCatalog();
            removeOldUpdates();
        } catch (Exception ex) {
            log.error("Update timer failed with error", ex);
        }
    }

    private void removeOldUpdates() {
        if (!updatesEnabled())
            return;
        val current = getCurrentVersion();
        catalog.getVersions()
                .stream()
                .filter(ver -> ver.compareTo(current) < 0)
                .forEach(ver -> {
                    File file = updateDir.resolve(ver.getFileName()).toFile();
                    File hashFile = updateDir.resolve(ver.getFileName() + ".md5").toFile();
                    if (file.exists())
                        file.delete();
                    if (hashFile.exists())
                        file.delete();
                });
    }

    private boolean hashesMatch(CatalogVersion ver, String hash) {
        if (StringUtil.isNullOrWhites(ver.getHash()))
            return true;
        if (StringUtil.isNullOrWhites(hash))
            return false;
        return ver.getHash().trim().equalsIgnoreCase(hash.trim());
    }

    private boolean updatesEnabled() {
        return !StringUtil.isNullOrWhites(updateBaseUrl);
    }

    private void downloadCatalog() {
        //Updates is disabled
        if (!updatesEnabled())
            return;
        File catFile = updateDir.resolve("catalog.json").toFile();
        try {
            IOUtil.writeFullyCloseAll(new FileOutputStream(catFile), getUrl("catalog.json").openStream());
            loadCatalog();
            downloadVersions();
        } catch (Exception ex) {
            log.error("Failed to download catalog: " + ex.getMessage());
        }
    }

    private void downloadVersions() throws IOException {
        forEachAvailUpdate((ver, file, hashFile, hash) -> {
            if (file.exists() && hashesMatch(ver, hash))
                return true;
            try {
                IOUtil.writeFullyCloseAll(new FileOutputStream(file), getUrl(ver.getFileName()).openStream());
                String md5 = MD5Util.calculateMD5Hex(file);
                IOUtil.writeFileFully(hashFile, md5);
                return true;
            } catch (Exception ex) {
                file.delete();
                log.error("Failed to download version: " + ex.getMessage());
                return false;
            }
        });

    }

    private void forEachAvailUpdate(VersionConsumer consumer) throws IOException {
        Versioned patched = getPatchedVersion();

        List<CatalogVersion> versionToCheck = catalog.getVersions()
                .stream()
                .filter(ver -> ver.compareTo(patched) > 0)
                .sorted().collect(Collectors.toList());

        for (CatalogVersion ver : versionToCheck) {
            File file = updateDir.resolve(ver.getFileName()).toFile();
            File hashFile = updateDir.resolve(ver.getFileName() + ".md5").toFile();
            String hash = "";
            if (hashFile.exists())
                hash = IOUtil.readFileFullyAsString(hashFile.getAbsolutePath());
            if (!consumer.accept(ver, file, hashFile, hash))
                break;
        }
    }

    private URL getUrl(String part) throws MalformedURLException {
        String url = StringUtil.trimr(updateBaseUrl, '/') + "/";
        return new URL(url + part);
    }

    @Override
    public Collection<Versioned> getVersions() {
        return catalog.getVersions()
                .stream()
                .map(cv -> new Version(cv.getVersion(), cv.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public void update(Consumer<String> messages) {
        try {
            forEachAvailUpdate((ver, file, hashFile, hash) -> {
                //Break and try and download it again
                if (!file.exists() || !hashesMatch(ver, hash))
                    return false;
                messages.accept("Exploding version: " + ver);
                try {
                    IOUtil.explodeZip(file.toPath(), updateDir.resolve("deploy"));
                    patched = ver;
                    return true;
                } catch (IOException ex) {
                    file.delete();
                    log.error("Failed to explode update: " + ex.getMessage());
                    return false;
                }
            });
        } catch (IOException ex) {
            log.error("Failed to update", ex);
        }
    }

    @Override
    public void restart() {
        Path currentRelativePath = Paths.get("").toAbsolutePath();
        String[] files = currentRelativePath.toFile().list();
        for (String file : files) {
            if (file.toLowerCase().matches(".*rules.*exe")
                    || file.equalsIgnoreCase("run.exe")
                    || file.equalsIgnoreCase("run.sh")
                    || file.equalsIgnoreCase("service.exe")
                    || file.equalsIgnoreCase("service.sh")) {
                try {
                    String cmd = currentRelativePath.resolve(file).toString();
                    new ProcessBuilder(cmd, "restart")
                            .directory(currentRelativePath.toFile())
                            .start();
                } catch (IOException e) {
                    log.error("Failed to restart: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean isRestartRequired() {
        Versioned current = getCurrentVersion();
        return patched != null && patched.compareTo(current) > 0;
    }

    @Override
    public Versioned getLatestVersion() {
        List<CatalogVersion> downloaded = new ArrayList<>();
        try {
            forEachAvailUpdate((ver, file, hashFile, hash) -> {
                if (file.exists() && hashesMatch(ver, hash))
                    downloaded.add(ver);
                return true;
            });
        } catch (IOException e) {
            log.error("Failed to get versions", e);
        }

        return downloaded
                .stream()
                .max(Comparator.naturalOrder())
                .map(ver -> (Versioned) ver)
                .orElse(getCurrentVersion());
    }

    @Override
    public Versioned getCurrentVersion() {
        if (versionProv == null)
            return zeroVersion;
        return versionProv.getCurrentVersion();
    }

    @Override
    public Versioned getPatchedVersion() {
        Versioned current = getCurrentVersion();
        //If we patched it return patched.
        if (patched != null && patched.compareTo(current) > 0)
            return patched;
        return current;
    }

    @Override
    public NodeInfoDbo getNodeInfo() {
        NodeInfoDbo ret = new NodeInfoDbo();
        ret.setAppName(appName);
        ret.setInstanceName(nodeName);
        ret.setDateTime(new Date());
        ret.setOs(System.getProperty("os.name"));
        ret.setVersion(getCurrentVersion().asString());

        try {
            InetAddress addr = getInetAddress();
            ret.setLocalIp(addr.getHostAddress());
            ret.setHostName(addr.getHostName());
            NetworkInterface network = NetworkInterface.getByInetAddress(addr);
            byte[] macb = network.getHardwareAddress();
            if (macb != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < macb.length; i++) {
                    sb.append(String.format("%02X%s", macb[i], (i < macb.length - 1) ? "-" : ""));
                }
                ret.setMac(sb.toString());
            }
        } catch (Exception e) {
            log.error("Unable to resolve host ip", e);
        }

        return ret;
    }

    private InetAddress getInetAddress() throws SocketException {
        InetAddress anyOther = null;
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                anyOther = inetAddress;
                String addr = inetAddress.getHostAddress();
                if (addr.contains("127.0.0.") || addr.contains(":") || netint.getHardwareAddress() == null)
                    continue;
                return inetAddress;
            }
        }
        return anyOther;
    }

    private void sendNodeInfo() {
        if (StringUtil.isNullOrWhites(updateInfoBaseUrl))
            return;
        try {
            NodeInfoDbo info = getNodeInfo();
            String url = StringUtil.trimr(updateInfoBaseUrl, '/') + "/api/v1/system/update/node";
            restTemplate.postForEntity(url, info, Void.class);
        } catch (Exception ex) {
            log.error("Failed to send node info", ex);
        }
    }

    @Override
    public void logNodeInfo(NodeInfo info) {
        NodeInfoDbo dbo = new NodeInfoDbo(info);
        log.error("Node Info: " + dbo.toString());
    }

    @FunctionalInterface
    interface VersionConsumer {
        boolean accept(CatalogVersion ver, File file, File hashFile, String hash);
    }
}
