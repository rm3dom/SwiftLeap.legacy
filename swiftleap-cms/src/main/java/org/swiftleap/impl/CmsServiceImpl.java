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
package org.swiftleap.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.swiftleap.cms.*;
import org.swiftleap.common.security.SecurityContext;
import org.swiftleap.common.util.IOUtil;
import org.swiftleap.common.util.MimeUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/06/03.
 */
@Service
public class CmsServiceImpl implements CmsService {
    static final Logger log = LoggerFactory.getLogger(CmsServiceImpl.class);

    ObjectMapper om = new ObjectMapper();
    @Value("${cms.repoDir}")
    String REPO_PATH;


    public CmsServiceImpl() {
        om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        om.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
    }

    @Override
    public CmsFolderNode getRoot() {
        CmsFolderNodeImpl folderItem = new CmsFolderNodeImpl("", "");
        return folderItem;
    }

    String parentPath(String fullPath) {
        if (fullPath.startsWith("/"))
            fullPath = fullPath.substring(1);
        if (fullPath.contains("/")) {
            return fullPath.substring(0, fullPath.lastIndexOf("/"));
        }
        return "";
    }

    String path(String fullPath) {
        if (fullPath.startsWith("/"))
            fullPath = fullPath.substring(1);
        if (fullPath.contains("/")) {
            fullPath = fullPath.substring(fullPath.lastIndexOf("/") + 1);
        }
        return fullPath;
    }

    @Override
    public CmsFolderNode getFolder(String path) {
        if (path == null)
            return null;
        if (path.isEmpty())
            return getRoot();
        String parentFullPath = parentPath(path);
        path = path(path);
        val ret = new CmsFolderNodeImpl(path, parentFullPath);
        log.debug("Got folder: {}", ret.getRealFullPath());
        return ret;
    }

    @Override
    public CmsNode getItem(CmsFolderNode folder, String relativePath) {
        return getItem(folder.getFullPath() + "/" + relativePath);
    }

    @Override
    public CmsNode getItem(String path) {
        if (path == null)
            return null;
        if (path.isEmpty())
            return getRoot();
        if (path.startsWith("/"))
            path = path.substring(1);
        File file = new File(getBasePath() + "/" + path);
        if (!file.exists())
            return null;
        String parentFullPath = parentPath(path);
        path = path(path);
        if (file.isDirectory())
            return new CmsFolderNodeImpl(path, parentFullPath);
        else
            return new CmsFileNodeImpl(path, parentFullPath);
    }

    private String getBasePath() {
        Integer tenantId = SecurityContext.getTenantId();
        return REPO_PATH + "/" + tenantId;
    }

    @Getter
    @Setter
    public static class FileMeta {
        String name;
        String title;
        String description;
        String mime;
    }

    class CmsFolderNodeImpl implements CmsFolderNode {
        String path;
        String parentFullPath;

        public CmsFolderNodeImpl(String path, String parentFullPath) {
            this.path = path;
            this.parentFullPath = parentFullPath;
        }

        public CmsFolderNodeImpl(String path, CmsFolderNodeImpl parent) {
            this.path = path;
            this.parentFullPath = parent.getFullPath();
        }

        @Override
        public boolean exists() {
            return new File(getRealFullPath()).exists();
        }

        @Override
        public boolean create() {
            return new File(getRealFullPath()).mkdirs();
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public String getFullPath() {
            if (parentFullPath.equals(""))
                return path;
            return parentFullPath + "/" + path;
        }

        String getRealFullPath() {
            return getBasePath() + "/" + getFullPath();
        }

        @Override
        public String getName() {
            return path;
        }


        @Override
        public Stream<CmsNode> getChildren() {
            File file = new File(getRealFullPath());
            List<CmsNode> items = new ArrayList<>();
            for (File child : file.listFiles()) {
                if (child.getName().endsWith(".cmf"))
                    continue;
                if (child.isDirectory())
                    items.add(new CmsFolderNodeImpl(child.getName(), this));
                else
                    items.add(new CmsFileNodeImpl(child.getName(), this));
            }
            return items.stream();
        }

        @Override
        public CmsFolderNode createFolder(String name) throws IOException {
            CmsFolderNodeImpl folderItem = new CmsFolderNodeImpl(name, this);
            folderItem.save();
            return folderItem;
        }

        @Override
        public CmsFileNode writeFile(FileRequest request) throws IOException {

            String path = request.getId();
            if (path == null || path.isEmpty())
                path = UUID.randomUUID().toString();

            String mime = request.getMime() == null || request.getMime().isEmpty()
                    ? MimeUtil.getFileName(request.getName())
                    : request.getMime();

            FileMeta meta = new FileMeta();
            meta.setName(request.getName());
            meta.setDescription(request.getDescription());
            meta.setTitle(request.getTitle());
            meta.setMime(mime);

            CmsFileNodeImpl fileItem = new CmsFileNodeImpl(path, this);
            fileItem.meta = meta;
            fileItem.data = request.getData();
            fileItem.save();

            return fileItem;
        }

        @Override
        public void delete() throws IOException {
            getChildren().forEach(item -> {
                try {
                    item.delete();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            File file = new File(getRealFullPath());
            if (!file.delete())
                file.deleteOnExit();
        }

        public void save() throws IOException {
            File file = new File(getRealFullPath());
            if (!file.exists() && !file.mkdirs())
                throw new IOException("Failed to create directories");
        }
    }

    class CmsFileNodeImpl implements CmsFileNode {
        public byte[] data;
        String path;
        String parentFullPath;
        FileMeta meta;

        public CmsFileNodeImpl(String path, CmsFolderNodeImpl parent, FileMeta meta, byte[] data) {
            this.path = path;
            this.parentFullPath = parent.getFullPath();
            this.meta = meta;
            this.data = data;
        }

        public CmsFileNodeImpl(String path, String parentFullPath) {
            this.path = path;
            this.parentFullPath = parentFullPath;
        }

        public CmsFileNodeImpl(String path, CmsFolderNodeImpl parent) {
            this.path = path;
            this.parentFullPath = parent.getFullPath();
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public String getFullPath() {
            return parentFullPath + "/" + path;
        }

        String getRealFullPath() {
            return getBasePath() + "/" + getFullPath();
        }

        @Override
        public String getName() {
            return getMeta().getName();
        }

        @Override
        public void delete() throws IOException {
            data = null;
            meta = null;

            File file = new File(getRealFullPath());
            if (!file.delete())
                file.deleteOnExit();
            File fileMeta = new File(getRealFullPath() + ".cmf");
            if (!fileMeta.delete())
                fileMeta.deleteOnExit();
        }

        @Override
        public String getDescription() {
            return getMeta().getDescription();
        }

        @Override
        public String getTitle() {
            return getMeta().getTitle();
        }

        @Override
        public byte[] getData() throws IOException {
            if (data == null)
                data = IOUtil.readFileFully(getRealFullPath()).toByteArray();
            return data;
        }

        FileMeta getMeta() {
            if (meta == null) {
                File file = new File(getRealFullPath() + ".cmf");
                if (!file.exists())
                    return new FileMeta();

                try {
                    meta = om.readValue(file, FileMeta.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return meta;
        }

        @Override
        public String getMime() {
            return getMeta().getMime();
        }

        public void save() throws IOException {
            if (data != null) {
                IOUtil.writeFileFully(getRealFullPath(), data);
            }

            if (meta != null) {
                om.writeValue(new File(getRealFullPath() + ".cmf"), meta);
            }
        }
    }
}
