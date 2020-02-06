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

import java.util.HashMap;
import java.util.Map;

/**
 * @author ruan
 */
public class MimeUtil {

    private final static Map<String, String> map = new HashMap<>();

    static {
        map.put("pdf", "application/pdf");
        map.put("tif", "image/tiff");
        map.put("tiff", "image/tiff");
        map.put("txt", "text/plain");
        map.put("csv", "text/csv");
        map.put("jpg", "image/jpeg");
        map.put("jpeg", "image/jpeg");
        map.put("png", "image/png");
        map.put("bmp", "image/bmp");
        map.put("raw", "application/octet-stream");
        map.put("doc", "application/vnd.ms-word");
        map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        map.put("xls", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        map.put("odt", "application/vnd.oasis.opendocument.text");
        map.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        map.put("html", "text/html");
        map.put("htm", "text/html");
        map.put("xml", "application/xml");
        map.put("json", "application/json");
    }

    public static String getExtForMime(String mime) {
        mime = mime.toLowerCase();
        for (String s : map.values()) {
            if (s.contains(mime)) return s;
        }
        return null;
    }

    public static String getExt(String ext) {
        if (ext == null) {
            return null;
        }
        return map.get(ext.toLowerCase());
    }

    public static String getFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            return getExt(fileName.substring(dot + 1));
        }
        return null;
    }
}
