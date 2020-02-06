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
 * A lazy singleton using the double check locking strategy.
 * Created by ruans on 2015/10/29.
 */
public class Singleton<E> {
    final Object mutex = this;
    volatile E instance;
    Supplier<E> supplier;

    public Singleton() {
    }

    public Singleton(Supplier<E> supplier) {
        this.supplier = supplier;
    }

    public Singleton(E instance) {
        this.instance = instance;
    }

    public void setSupplier(Supplier<E> supplier) {
        this.supplier = supplier;
    }

    public E get() {
        if (instance != null) return instance;
        synchronized (mutex) {
            if (instance != null) return instance;
            if (supplier != null) instance = supplier.get();
        }
        return instance;
    }

    public boolean isSet() {
        return get() != null;
    }

    public void set(E instance) {
        synchronized (mutex) {
            this.instance = instance;
        }
    }
}
