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
package org.swiftleap.common.security;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A thread safe tenanted storage similar to thread local.
 *
 * @param <T>
 */
public class TenantLocal<T> {
    public Map<Integer, T> map = new HashMap<>();

    public synchronized T get() {
        return map.get(getTenantId());
    }

    public synchronized T get(Function<Integer, T> onMiss) {
        final Integer tenantId = getTenantId();
        T ret = map.get(tenantId);
        if (ret == null) {
            ret = onMiss.apply(tenantId);
            map.put(tenantId, ret);
        }
        return ret;
    }

    public synchronized T set(T t) {
        return map.put(getTenantId(), t);
    }

    public synchronized boolean isSet() {
        return map.containsKey(getTenantId());
    }

    public synchronized Iterator<Map.Entry<Integer, T>> values() {
        return new ArrayList<>(map.entrySet()).iterator();
    }

    public Integer getTenantId() {
        return SecurityContext.getTenantId();
    }

    public void forEach(BiConsumer<Integer, T> action) {
        values().forEachRemaining(e -> action.accept(e.getKey(), e.getValue()));
    }
}
