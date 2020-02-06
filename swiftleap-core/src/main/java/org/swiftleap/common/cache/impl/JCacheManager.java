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

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JCache implementation.
 * <p/>
 * Created by ruans on 2015/11/25.
 */
public final class JCacheManager implements SimpleCacheManager {
    final Map<String, SimpleCache<?, ?>> caches = new HashMap<>();
    //final Subject<?> invalidateAllSubject = new Subject<>();

    @Override
    public boolean isJCachePresent() {
        try {
            return Caching.getCachingProvider() != null;
        } catch (CacheException e) {
            return false;
        }
    }

    @Override
    public void invalidateAll() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager mgr = cachingProvider.getCacheManager();
        mgr.getCacheNames().forEach((s) -> Optional.of(mgr.getCache(s)).ifPresent(Cache::removeAll));
        //invalidateAllSubject.notify(null);
    }

    /*@Override
    public SubjectRegistry<?> getInvalidateAllSubject() {
        return invalidateAllSubject;
    }
    */

    public <K, V> Cache<K, V> getCache(String name, Class<K> keyClass, Class<V> valueClass) {
        Cache<K, V> cache = Caching.getCache(name, keyClass, valueClass);
        if (cache != null) return cache;

        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager mgr = cachingProvider.getCacheManager();
        MutableConfiguration<K, V> config = new MutableConfiguration<>();
        config.setTypes(keyClass, valueClass);
        config.setStoreByValue(true);
        config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
        cache = mgr.createCache(name, config);
        return cache;
    }

    @Override
    public <K, V> SimpleCache<K, V> getSimpleCache(String name,
                                                   Class<K> keyClass,
                                                   Class<V> valueClass,
                                                   final Miss<K, V> miss) {
        final Cache<K, V> cache = getCache(name, keyClass, valueClass);
        return new SimpleCache<K, V>() {
            @Override
            public V getEntry(K key) {
                V v = cache.get(key);
                if (v != null) return v;
                if (miss == null) return null;
                v = miss.miss(key);
                if (v == null) return null;
                cache.put(key, v);
                return v;
            }

            @Override
            public void putEntry(K key, V value) {
                if (value == null) {
                    cache.remove(key);
                    return;
                }
                cache.put(key, value);
            }

            @Override
            public void removeEntry(K key) {
                cache.remove(key);
            }

            @Override
            public void invalidate() {
                cache.removeAll();
            }
        };
    }
}
