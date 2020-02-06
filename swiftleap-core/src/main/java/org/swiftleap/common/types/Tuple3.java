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
public class Tuple3<V1, V2, V3> extends Tuple2<V1, V2> {
    V3 value3;

    public Tuple3() {
    }

    public Tuple3(V1 value1, V2 value2, V3 value3) {
        super(value1, value2);
        this.value3 = value3;
    }

    public V3 getValue3() {
        return value3;
    }

    public void setValue3(V3 value3) {
        this.value3 = value3;
    }

    @Override
    public String toString() {
        return "Tuple3{" +
                "value1=" + value1 +
                ", value2=" + value2 +
                ", value3=" + value3 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple3)) return false;
        if (!super.equals(o)) return false;

        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;

        return Objects.equals(value3, tuple3.value3);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value3 != null ? value3.hashCode() : 0);
        return result;
    }
}
