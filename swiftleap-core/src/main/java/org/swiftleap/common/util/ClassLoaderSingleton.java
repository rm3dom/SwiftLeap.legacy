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

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * A singleton per class loader.
 * <p/>
 * Created by ruans on 2015/10/29.
 */
public class ClassLoaderSingleton<E> extends Singleton<E> {
    Map<ClassLoader, E> map = new WeakHashMap<>(0);

    public ClassLoaderSingleton() {

    }

    public ClassLoaderSingleton(Supplier<E> supplier) {
        super(supplier);
    }

    public E get(ClassLoader classLoader) {
        //NOTE: This portion is not threadsafe, the singletons should be set only once per application.
        synchronized (mutex) {
            E ret = map.get(classLoader);
            if (ret != null) return ret;
            if (supplier == null) return null;
            ret = supplier.get();
            if (ret == null) return null;
            map.put(classLoader, ret);
            return ret;
        }
    }

    @Override
    public E get() {
        return get(ReflectionUtil.getContextClassLoader());
    }

    @Override
    public void set(E instance) {
        set(instance, ReflectionUtil.getContextClassLoader());
    }

    public void set(E instance, ClassLoader classLoader) {
        synchronized (mutex) {
            map.put(classLoader, instance);
        }
    }
}
