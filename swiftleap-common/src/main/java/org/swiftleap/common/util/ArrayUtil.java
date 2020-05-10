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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Additional array utils to {@code Arrays}.
 * Created by ruans on 2015/08/19.
 */
public class ArrayUtil {
    public static <T> boolean nullOrEmpty(T[] a) {
        return a == null || a.length == 0;
    }

    @SuppressWarnings("varargs")
    public static <T> T[] concat(T[] a, T... b) {
        if (a == null)
            return b;

        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    /*
    public static <T> Tuple2<T, T> flip(T a, T b) {
        return new Tuple2<>(b, a);
    }
     */

    @SuppressWarnings("varargs")
    public static <T> T[] flip(T[] a, T... b) {
        if (b == null)
            return null;

        if (a == null || a.length != b.length || a == b)
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), b.length);

        for (int i = b.length - 1; i >= 0; i--)
            a[i] = b[b.length - 1 - i];

        return a;
    }

    public static <T> Map<T, T> asMap(T[][] arr) {
        return Stream.of(arr).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Set<T> asSet(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
