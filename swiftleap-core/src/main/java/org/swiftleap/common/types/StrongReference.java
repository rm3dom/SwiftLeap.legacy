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

/**
 * Useful for final variables, that need to change.
 * <p/>
 * Created by ruan on 2015/08/11.
 */
public class StrongReference<T> {
    T t;

    public StrongReference(T t) {
        this.t = t;
    }

    public StrongReference() {
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }

    public boolean isNull() {
        return t == null;
    }
}
