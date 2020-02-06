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


package org.swiftleap.common.cache;


import org.swiftleap.common.cache.impl.SimpleCacheManagerImpl;
import org.swiftleap.common.util.ClassLoaderSingleton;
import org.swiftleap.common.util.ReflectionUtil;

/**
 * A simple cache factory that will make use of jcache if present.
 * <p>
 * The cache factory will create a manager per class loader and caches are kept per manager instance.
 * </p>
 * Created by ruans on 2015/11/25.
 */
public final class SimpleCacheFactory {
    static final ClassLoaderSingleton<SimpleCacheManager> instance
            = new ClassLoaderSingleton<>(SimpleCacheFactory::newManager);

    private static SimpleCacheManager newManager() {
        ClassLoader loader = ReflectionUtil.getContextClassLoader();
        try {
            SimpleCacheManager manager = (SimpleCacheManager) loader
                    .loadClass("org.swiftleap.common.cache.impl.JCacheManager")
                    .newInstance();
            if (manager.isJCachePresent()) return manager;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            //Not present
        }
        return new SimpleCacheManagerImpl();
    }

    public static SimpleCacheManager getManager() {
        return instance.get();
    }

    public static <K, V> SimpleCache<K, V> getSimpleCache(String name,
                                                          Class<K> keyClass,
                                                          Class<V> valueClass) {
        return instance.get().getSimpleCache(name, keyClass, valueClass);
    }

    public static <K, V> SimpleCache<K, V> getSimpleCache(String name,
                                                          Class<K> keyClass,
                                                          Class<V> valueClass,
                                                          Miss<K, V> miss) {
        return instance.get().getSimpleCache(name, keyClass, valueClass, miss);
    }
}
