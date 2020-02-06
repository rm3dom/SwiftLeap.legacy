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

import java.util.Objects;

/**
 * Created by ruans on 2015/08/22.
 */
public class Tuple4<V1, V2, V3, V4> extends Tuple3<V1, V2, V3> {
    V4 value4;

    public Tuple4() {
    }

    public Tuple4(V1 value1, V2 value2, V3 value3, V4 value4) {
        super(value1, value2, value3);
        this.value4 = value4;
    }

    public V4 getValue4() {
        return value4;
    }

    public void setValue4(V4 value4) {
        this.value4 = value4;
    }

    @Override
    public String toString() {
        return "Tuple3{" +
                "value1=" + value1 +
                ", value2=" + value2 +
                ", value3=" + value3 +
                ", value4=" + value4 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple4)) return false;
        if (!super.equals(o)) return false;

        Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;

        return Objects.equals(value4, tuple4.value4);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value4 != null ? value4.hashCode() : 0);
        return result;
    }
}
