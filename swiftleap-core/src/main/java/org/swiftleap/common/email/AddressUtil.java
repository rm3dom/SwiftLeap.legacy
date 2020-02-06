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
package org.swiftleap.common.email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ruan on 2015/06/04.
 */
public class AddressUtil {
    public static String getName(String from) {
        try {
            Pattern pattern = Pattern.compile("([^<]+)[<]([^>]+)");
            Matcher matcher = pattern.matcher(from);
            matcher.find();
            return matcher.group(1).trim();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getEmailAddress(String from) {
        try {
            Pattern pattern = Pattern.compile("([^<]+)[<]([^>]+)");
            Matcher matcher = pattern.matcher(from);
            matcher.find();
            return matcher.group(2).trim();
        } catch (Exception ex) {
            return from.trim();
        }
    }


}
