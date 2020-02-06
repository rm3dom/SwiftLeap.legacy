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

import java.util.function.Supplier;

/**
 * Created by ruans on 2018/01/19.
 */
public class NullUtil {
    public static <T> T whenNull(T val, T def) {
        return val == null ? def : val;
    }

    public static <T> T whenNull(T val, Supplier<T> func) {
        return val == null ? func.get() : val;
    }

    public static <T> T notNull(T... val) {
        for (int i = 0; i < val.length; i++) {
            if (val[i] != null)
                return val[i];
        }
        return null;
    }
}
