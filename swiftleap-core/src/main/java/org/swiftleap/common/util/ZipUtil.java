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

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static Iterator<Entry> unpack(InputStream in) throws IOException {
        ZipInputStream si = new ZipInputStream(in);
        return new Iterator<Entry>() {
            ZipEntry nextZe;

            @Override
            public boolean hasNext() {
                try {
                    nextZe = si.getNextEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return nextZe != null;
            }

            @Override
            public Entry next() {
                if (nextZe == null)
                    return null;
                ByteArrayOutputStream buf;
                try {
                    buf = IOUtil.readFully(si);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new ByteArrayEntry(nextZe.getName(), buf.toByteArray());
            }
        };
    }

    public static void pack(Entry entry, OutputStream out) throws IOException {
        pack(Arrays.asList(entry), out);
    }

    public static void pack(Iterable<Entry> entries, OutputStream out) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(out)) {
            entries
                    .forEach(entry -> {
                        ZipEntry zipEntry = new ZipEntry(entry.getName());
                        try {
                            zs.putNextEntry(zipEntry);
                            IOUtil.writeFullyCloseIn(zs, entry.getInputStream());
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
    }

    public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
    }

    public interface Entry {
        String getName();

        InputStream getInputStream();
    }

    @Getter
    @Setter
    public static class ByteArrayEntry implements Entry {
        String name;
        byte[] data;

        public ByteArrayEntry(String name, byte[] data) {
            this.name = name;
            this.data = data;
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }
    }
}