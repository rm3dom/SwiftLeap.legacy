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


import org.swiftleap.common.types.EnumValueType;

import java.util.EnumSet;

/**
 * Find enums by various criteria.
 * <p>
 * {@code
 * enum Alphabet {A,B,C}
 * Alphabet a = EnumUtil.byOrdinalDef(99, Alphabet.A);
 * a = EnumUtil.byOrdinalDef("Z", Alphabet.A);
 * }
 *
 * @author ruan
 */
public final class EnumUtil {

    public static <E extends Enum<E>> E byName(Class<E> enumClass, String name) {
        if (name == null) {
            return null;
        }
        for (E e : EnumSet.allOf(enumClass)) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getFistEnumName(Class<?> clazz) {
        if (!clazz.isEnum())
            return "";
        Object[] enums = EnumSet.allOf((Class<Enum>) clazz).toArray();
        if (enums.length < 1)
            return "";
        return enums[0].toString();
    }

    public static <E extends Enum<E>> E byNameDef(String name, E def) {
        if (name == null) {
            return def;
        }
        for (E e : EnumSet.allOf(def.getDeclaringClass())) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return def;
    }


    public static <E extends Enum<E>> E byOrdinal(Class<E> enumClass, int ordinal) {
        for (E e : EnumSet.allOf(enumClass)) {
            if (e.ordinal() == ordinal) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<E>> E byOrdinal(int ordinal, E def) {
        for (E e : EnumSet.allOf(def.getDeclaringClass())) {
            if (e.ordinal() == ordinal) {
                return e;
            }
        }
        return def;
    }

    public static <E extends Enum<E> & EnumValueType<V>, V> E byValue(Class<E> enumClass, V value) {
        if (value == null) {
            return null;
        }
        for (E e : EnumSet.allOf(enumClass)) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<E> & EnumValueType<V>, V> E byValue(V value, E def) {
        E e = byValue(def.getDeclaringClass(), value);
        if (e != null) {
            return e;
        }
        return def;
    }

    public static <E extends Enum<E>> E byToString(Class<E> enumClass, String value) {
        if (value == null) {
            return null;
        }
        for (E e : EnumSet.allOf(enumClass)) {
            if (e.toString().equals(value)) {
                return e;
            }
        }
        return null;
    }

    public static <E extends Enum<E>> E byToString(String value, E def) {
        E e = byToString(def.getDeclaringClass(), value);
        if (e != null) {
            return e;
        }
        return def;
    }
}
