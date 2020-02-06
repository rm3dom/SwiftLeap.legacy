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
package org.swiftleap.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ruans on 2017/06/03.
 */
public class IOUtil {
    static final Logger log = LoggerFactory.getLogger(IOUtil.class);

    public static InputStream getFileStream(ClassLoader cl, String path) throws IOException {
        if (path.startsWith("classpath:")) {
            path = path.replace("classpath:", "");
            return cl.getResourceAsStream(path);
        }
        File ret = new File(path);
        if (!ret.exists()) {
            ret = new File("../" + path);
        }
        return new FileInputStream(ret);
    }

    public static InputStream getFileStream(String path) throws IOException {
        return getFileStream(ReflectionUtil.getContextClassLoader(), path);
    }

    public static ByteArrayOutputStream readFileFully(String file) throws IOException {
        return readFully(new File(file));
    }

    public static String readFileFullyAsString(String file) throws IOException {
        ByteArrayOutputStream bout = readFileFully(file);
        return new String(bout.toByteArray(), StandardCharsets.UTF_8);
    }

    public static ByteArrayOutputStream readFully(File file) throws IOException {
        return readFullyClose(new FileInputStream(file));
    }

    public static ByteArrayOutputStream readFullyClose(InputStream in) throws IOException {
        try {
            return readFully(in);
        } finally {
            in.close();
        }
    }

    public static byte[] toPng(BufferedImage image) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
        ImageIO.write(image, "png", bout);
        return bout.toByteArray();
    }

    public static ByteArrayOutputStream readFully(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buff = new byte[2048];
        int read = -1;
        while ((read = in.read(buff)) > -1) {
            bout.write(buff, 0, read);
        }
        return bout;
    }

    public static String readFullyAsString(InputStream in) throws IOException {
        ByteArrayOutputStream bout = readFully(in);
        return new String(bout.toByteArray(), StandardCharsets.UTF_8);
    }

    public static void writeFileFully(String file, byte[] data) throws IOException {
        try (FileOutputStream fout = new FileOutputStream(new File(file))) {
            fout.write(data);
        }
    }

    public static void writeFileFully(File file, String data) throws IOException {
        try (FileOutputStream fout = new FileOutputStream(file)) {
            fout.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void writeFileFully(File file, OutputStream bout) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            byte[] buff = new byte[2048];
            int read = -1;
            while ((read = in.read(buff)) > -1) {
                bout.write(buff, 0, read);
            }
        }
    }

    public static void writeFileFully(File file, InputStream in) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            byte[] buff = new byte[2048];
            int read = -1;
            while ((read = in.read(buff)) > -1) {
                out.write(buff, 0, read);
            }
        }
    }

    public static void writeFully(OutputStream out, byte[] data) throws IOException {
        out.write(data);
    }

    public static void writeFullyCloseAll(OutputStream out, InputStream in) throws IOException {
        try {
            writeFullyCloseIn(out, in);
        } finally {
            out.close();
        }
    }

    public static void writeFullyCloseIn(OutputStream out, InputStream in) throws IOException {
        try {
            byte[] buff = new byte[2048];
            int read = -1;
            while ((read = in.read(buff)) > -1) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
        }
    }

    public static boolean deleteFile(File file) {
        return deleteFile(file.getAbsolutePath());
    }

    public static boolean deleteFile(String file) {
        try {
            Files.delete(FileSystems.getDefault().getPath(file));
        } catch (IOException e) {
            log.warn("Unable to delete file:" + file, e);
            return false;
        }
        return true;
    }


    /**
     * Delete all children of dir.
     *
     * @param dir
     * @return
     */
    public static boolean deleteAllFiles(File dir) {
        if (!dir.exists())
            return false;

        for (String s : dir.list()) {
            File f = new File(dir + File.separator + s);
            if (f.isDirectory()) {
                deleteAllFiles(f);
                if (!f.delete())
                    return false;
            } else {
                if (!f.delete())
                    return false;
            }
        }

        return true;
    }

    public static void explodeZip(InputStream zipFile, Path toDir) throws IOException {
        byte[] buffer = new byte[2046];
        try (ZipInputStream zis = new ZipInputStream(zipFile)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                File newFile = toDir.resolve(fileName).toFile();
                log.debug("Exploding: " + newFile);
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
            zis.closeEntry();
        }
    }

    public static void explodeZip(Path zipFile, Path toDir) throws IOException {
        log.debug("Exploding: " + zipFile + " to " + toDir);
        explodeZip(new FileInputStream(zipFile.toFile()), toDir);
    }
}
