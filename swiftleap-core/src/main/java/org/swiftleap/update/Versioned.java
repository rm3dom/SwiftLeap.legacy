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
package org.swiftleap.update;


public interface Versioned extends Comparable<Versioned> {
    static int parse(String version, int part) {
        String[] parts = version.split("[.]");
        if (parts.length > part) {
            return Integer.parseInt(parts[part]);
        }
        return 0;
    }

    static String parseOther(String version, int part) {
        String[] parts = version.split("[.]");
        if (parts.length > part) {
            return parts[part];
        }
        return "";
    }

    String getVersion();

    String getDescription();

    default int getMajor() {
        return parse(getVersion(), 0);
    }

    default int getMinor() {
        return parse(getVersion(), 1);
    }

    default int getPatch() {
        return parse(getVersion(), 2);
    }

    default String getOther() {
        String[] parts = getVersion().split("[.]");
        if (parts.length > 3) {
            return parts[3];
        }

        return "";
    }

    @Override
    default int compareTo(Versioned other) {
        int res = Integer.compare(getMajor(), other.getMajor());
        if (res != 0)
            return res;
        res = Integer.compare(getMinor(), other.getMinor());
        if (res != 0)
            return res;
        return Integer.compare(getPatch(), other.getPatch());
    }

    default int asHash() {
        int hash = (getMajor() * 1000000) + (getMinor() * 1000) + getPatch();
        hash ^= getOther().hashCode();
        hash ^= getDescription().hashCode();
        return Math.abs(hash);
    }

    default String asString() {
        String ret = getVersion();
        if (getDescription() != null && !getDescription().isEmpty())
            ret += " - " + getDescription();
        return ret;
    }
}
