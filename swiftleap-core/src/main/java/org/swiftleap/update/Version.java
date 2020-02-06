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

public class Version implements Versioned {
    private int major;
    private int minor;
    private int patch;
    private String other;
    private String description;

    public Version(int major, int minor, int patch, String other, String description) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.other = other;
        this.description = description;
    }

    public Version(String version, String description) {
        this.major = Versioned.parse(version, 0);
        this.minor = Versioned.parse(version, 1);
        this.patch = Versioned.parse(version, 2);
        this.other = Versioned.parseOther(version, 3);
        this.description = description;
    }

    public Version(Versioned version) {
        this.major = version.getMajor();
        this.minor = version.getMinor();
        this.patch = version.getPatch();
        this.other = version.getOther();
        this.description = version.getDescription();
    }

    @Override
    public String getVersion() {
        String s = String.format("%s.%s.%s", getMajor(), getMinor(), getPatch());
        if (getOther() != null && !getOther().isEmpty())
            s += "." + getOther();
        return s;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getOther() {
        return other;
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public int hashCode() {
        return asHash();
    }
}
