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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.swiftleap.common.security.SecurityContext;

/**
 * Created by ruans on 2017/06/11.
 */
public class PhoneUtil {
    static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public static String formatE164(String number, String defaultCountryCode) throws NumberParseException {
        if (defaultCountryCode == null || defaultCountryCode.isEmpty())
            defaultCountryCode = SecurityContext.getCountryCode();
        Phonenumber.PhoneNumber pn = phoneUtil.parse(number, defaultCountryCode);
        return phoneUtil.format(pn, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    public static String formatE164(String number) throws NumberParseException {
        String defaultCountryCode = SecurityContext.getCountryCode();
        return formatE164(number, defaultCountryCode);
    }

    public static String reverse(String s) {
        if (s == null)
            return null;
        char[] pn = s.toCharArray();
        StringBuilder ret = new StringBuilder();
        for (int i = pn.length - 1; i >= 0; i--)
            ret.append(pn[i]);
        return ret.toString();
    }

    public static String formatDb(String number, String defaultCountryCode) throws NumberParseException {
        if (number == null || number.trim().isEmpty())
            return null;
        return reverse(formatE164(number, defaultCountryCode));
    }

    public static String formatDb(String number) throws NumberParseException {
        if (number == null || number.trim().isEmpty())
            return null;
        return reverse(formatE164(number));
    }
}
