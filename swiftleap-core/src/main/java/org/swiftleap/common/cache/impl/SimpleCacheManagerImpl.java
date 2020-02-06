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


package org.swiftleap.common.cache.impl;


import org.swiftleap.common.cache.Miss;
import org.swiftleap.common.cache.SimpleCache;
import org.swiftleap.common.cache.SimpleCacheManager;
import org.swiftleap.common.collection.SoftHashMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation.
 * <p/>
 * Created by ruans on 2015/11/25.
 */
final public class SimpleCacheManagerImpl implements SimpleCacheManager {
    final Map<String, SimpleCache<?, ?>> caches = new HashMap<>();
    /*
    final Subject<?> invalidateAllSubject = new Subject<>();

    @Override
    public SubjectRegistry<?> getInvalidateAllSubject() {
        return invalidateAllSubject;
    }
    */

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <K, V> SimpleCache<K, V> getSimpleCache(String name,
                                                                Class<K> keyClass,
                                                                Class<V> valueClass,
                                                                Miss<K, V> miss) {
        String cacheName = name + "#" + valueClass.getName();
        SimpleCache<?, ?> cache = caches.get(cacheName);
        if (cache != null) {
            return (SimpleCache<K, V>) cache;
        }
        SimpleCache<K, V> newCache = new SoftCache<>(miss);
        caches.put(cacheName, newCache);
        return newCache;
    }

    @Override
    public synchronized void invalidateAll() {
        caches.forEach((k, v) -> v.invalidate());
        //invalidateAllSubject.notify(null);
    }

    class SoftCache<K, V> extends SoftHashMap<K, V> implements SimpleCache<K, V> {
        final Object mutex = this;
        Miss<K, V> miss;

        public SoftCache(Miss<K, V> miss) {
            this.miss = miss;
        }

        /* We do not synchronize the whole method as it may be a performance hit. */
        @Override
        public V getEntry(K key) {
            V v = null;
            synchronized (mutex) {
                v = super.get(key);
            }
            if (v != null) return v;
            if (miss == null) return null;
            v = miss.miss(key);
            if (v == null) return null;

            synchronized (mutex) {
                super.put(key, v);
            }
            return v;
        }

        @Override
        public void putEntry(K key, V value) {
            if (value == null) {
                removeEntry(key);
                return;
            }
            synchronized (mutex) {
                super.put(key, value);
            }
        }

        @Override
        public void removeEntry(K key) {
            synchronized (mutex) {
                super.remove(key);
            }
        }

        @Override
        public void invalidate() {
            synchronized (mutex) {
                super.clear();
            }
        }
    }
}
