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
package org.swiftleap.common.types;


import org.swiftleap.common.util.WordUtils;

import java.lang.reflect.ParameterizedType;

/**
 * @author ruan
 */
public interface EnumValueType<V> {
    V getValue();

    /**
     * This will do strict type checking and throw an assertion error if it is of different types.
     *
     * @param v the value to compare
     * @return equals
     */
    default boolean valueEquals(V v) {
        if (v == getValue()) return true;
        if (v == null) return false;
        //Make sure its of the same type
        assert getValue().getClass().equals(v.getClass());
        return getValue().equals(v);
    }


    default Class<?> valueClass() {
        return (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    default String toPrettyString() {
        return WordUtils.capitalizeFully(toString().replace("_", " "));
    }
}
