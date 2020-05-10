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

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by ruans on 2018/01/09.
 */
public class StringUtil {
    public static String join(String sep, Collection<?> col) {
        return join(sep, col.toArray());
    }

    public static String join(String sep, Object[] values) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            ret.append(values[i]);
            if (i < values.length - 1)
                ret.append(sep);
        }
        return ret.toString();
    }

    public static String triml(String str, char c) {
        while (str.length() > 0 && str.charAt(0) == c)
            str = str.substring(1);
        return str;
    }

    public static String trimr(String str, char c) {
        while (str.length() > 0 && str.charAt(str.length() - 1) == c)
            str = str.substring(0, str.length() - 1);
        return str;
    }

    public static String trim(String str, char c) {
        return triml(trimr(str, c), c);
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNullOrWhites(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String firstNonBlank(String... args) {
        for (String arg : args) {
            if (!isNullOrWhites(arg))
                return arg;
        }
        return "";
    }

    /**
     * SQL LIKE comparison.
     *
     * @param str  the string to match
     * @param expr a like expression
     * @return
     */
    public static boolean like(final String str, final String expr) {
        Pattern p = likePattern(expr);
        return p.matcher(str).matches();
    }

    /**
     * Convert a SQL like pattern to a regular expression.
     *
     * @param expr
     * @return
     */
    public static Pattern likePattern(final String expr) {
        String regex = quoteRegex(expr);
        regex = regex.replace("_", ".").replace("%", ".*?");
        return Pattern.compile(regex,
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

    /**
     * Quote a regex string.
     *
     * @param s
     * @return
     */
    public static String quoteRegex(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String cannot be null");
        }

        int len = s.length();
        if (len == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if ("[](){}.*+?$^|#\\".indexOf(c) != -1) {
                sb.append("\\");
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
