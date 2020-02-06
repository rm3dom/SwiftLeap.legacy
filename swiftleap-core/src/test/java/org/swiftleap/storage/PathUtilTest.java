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
package org.swiftleap.storage;


import org.junit.Assert;
import org.junit.Test;

public class PathUtilTest {

    @Test
    public void testJoin() {
        String s = StorageKt.joinPath(0, "1/", 2);
        Assert.assertEquals("0/1/2", s);

        s = StorageKt.joinPath(0, "1", 2);
        Assert.assertEquals("0/1/2", s);

        s = StorageKt.joinPath("1/");
        Assert.assertEquals("1/", s);

        s = StorageKt.joinPath("1");
        Assert.assertEquals("1", s);

        s = StorageKt.joinPath("1", "/", "/");
        Assert.assertEquals("1/", s);
    }

    @Test
    public void testBasename() {
        String s = StorageKt.basename("");
        Assert.assertEquals("", s);

        s = StorageKt.basename("/");
        Assert.assertEquals("/", s);

        s = StorageKt.basename("foo/bar");
        Assert.assertEquals("bar", s);
    }

    @Test
    public void testDirname() {
        String s = StorageKt.dirname("");
        Assert.assertEquals("", s);

        s = StorageKt.dirname("/");
        Assert.assertEquals("/", s);

        s = StorageKt.dirname("foo/bar");
        Assert.assertEquals("foo", s);
    }
}
