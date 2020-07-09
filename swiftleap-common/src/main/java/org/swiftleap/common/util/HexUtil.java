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

/**
 * @author ruan
 */
public class HexUtil {

    public static String getHex(byte... data) {
        String s = "";
        for (byte b : data) {
            int i = b & 0xFF;
            String h = Integer.toString(i, 16);
            if (h.length() < 2) {
                h = "0" + h;
            }
            s += h;
        }
        return s;
    }

    public static String getHexPlus(byte other, byte... data) {
        String s = getHex(other);
        for (byte b : data) {
            int i = b & 0xFF;
            String h = Integer.toString(i, 16);
            if (h.length() < 2) {
                h = "0" + h;
            }
            s += h;
        }
        return s;
    }

    public static byte[] fromHex(String hex) {
        byte[] ret = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            String c2 = "" + hex.charAt(i) + hex.charAt(i + 1);
            ret[i / 2] = (byte) Integer.parseInt(c2, 16);
        }
        return ret;
    }
}
