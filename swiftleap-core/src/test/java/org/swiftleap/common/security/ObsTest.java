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
package org.swiftleap.common.security;

import org.junit.Assert;
import org.junit.Test;

public class ObsTest {

    @Test
    public void testObs() {

        String test = "phdjwtsigningkey";
        String encrypted = Obs.encrypt(test);
        Assert.assertEquals("HkwCBRhk+Wan7tndImfVT7Z7p7btiVWXO6x78JeGcik=", encrypted);
        String decrypted = Obs.decrypt(encrypted);
        Assert.assertEquals(test, decrypted);

        System.out.println("obs:" + Obs.encrypt("Coffee350"));
        System.out.println(Obs.decrypt("Dm0mrqdi22C+tU1DI79V8w=="));
    }
}
