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
package org.swiftleap.common.codegen;

import lombok.Data;
import org.junit.Test;
import org.swiftleap.common.codegen.anotate.CGDefault;
import org.swiftleap.common.codegen.anotate.CGIgnore;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Created by ruans on 2017/06/17.
 */
public class CodeGeneratorTest {
    @Test
    public void testGenKotlin() throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Contact.class);

        CodeGenerator.generate(Language.KOTLINJS, System.out, classes);
    }

    @Test
    public void testGenElm() throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Contact.class);

        CodeGenerator.generate(Language.ELM, System.out, classes);
    }

    @Test
    public void testOther() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                String addr = inetAddress.getHostAddress();
                if (addr.contains("127.0.0.") || addr.contains(":"))
                    continue;
                System.out.println(inetAddress.getHostAddress());
            }
        }
    }

    public static enum Gender {
        Male,
        Female
    }

    @Data
    public static class ContactPoint {
        Integer id;
        String type;
        @CGIgnore
        String detail;
    }

    @Data
    public static class Contact {
        HashMap<String, String> map;
        Integer id;
        @CGDefault(all = "\"Ruan\"")
        String name;
        @CGDefault(all = "\"Strydom\"")
        String surname;
        @CGDefault(elm = "\"Male\"", kotlin = "Gender.Female")
        Gender gender;
        List<Gender> genders;
        int age;
        Date dob;
        List<ContactPoint> contactPoints;
        ContactPoint contactPoint;
        BigDecimal points;
        byte[] data;
    }
}

