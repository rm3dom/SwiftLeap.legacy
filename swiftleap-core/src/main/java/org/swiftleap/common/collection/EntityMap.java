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
package org.swiftleap.common.collection;


import org.swiftleap.common.persistance.GenericEntity;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

/**
 * An entity map.
 *
 * @author ruan
 */
public class EntityMap<I extends Serializable, T extends GenericEntity<I>> extends HashMap<I, T> {

    public EntityMap(Collection<T> list) {
        super(list.size());
        addAll(list);
    }

    public EntityMap(Iterator<? extends T> i) {
        while (i.hasNext()) put(i.next());
    }

    public EntityMap(Iterable<? extends T> i) {
        i.forEach(this::put);
    }

    public EntityMap(Stream<? extends T> i) {
        i.forEach(this::put);
    }

    public EntityMap(Map<I, T> i) {
        super(i);
    }

    public EntityMap() {
    }

    public EntityMap(int initialCapacity) {
        super(initialCapacity);
    }


    public EntityMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public static <I extends Serializable, T extends GenericEntity<I>> EntityMap<I, T> from(Iterable<T> c) {
        if (c == null) return new EntityMap<>();
        return new EntityMap<>(c);
    }

    public static <I extends Serializable, T extends GenericEntity<I>> EntityMap<I, T> from(Stream<T> c) {
        if (c == null) return new EntityMap<>();
        return new EntityMap<>(c);
    }

    public final void put(T t) {
        put(t.getId(), t);
    }

    public final void addAll(Collection<T> list) {
        for (T e : list) {
            put(e.getId(), e);
        }
    }

    public final void removeAll(Collection<T> list) {
        for (T e : list) {
            remove(e.getId());
        }
    }

    public List<T> valueList() {
        return new ArrayList<>(values());
    }

    public List<T> list() {
        return valueList();
    }


    public Set<T> valueSet() {
        return new HashSet<>(values());
    }
}
