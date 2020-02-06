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

/**
 * SimpleCacheManager spec.
 * Created by ruans on 2015/11/25.
 */
public interface SimpleCacheManager {
    default boolean isJCachePresent() {
        return false;
    }

    /**
     * Remove all entries from all known caches and notify all listeners.
     */
    void invalidateAll();

    //SubjectRegistry<?> getInvalidateAllSubject();

    default <K, V> SimpleCache<K, V> getSimpleCache(String name, Class<K> keyClass, Class<V> valueClass) {
        return getSimpleCache(name, keyClass, valueClass, null);
    }

    <K, V> SimpleCache<K, V> getSimpleCache(String name, Class<K> keyClass, Class<V> valueClass, Miss<K, V> miss);
}
