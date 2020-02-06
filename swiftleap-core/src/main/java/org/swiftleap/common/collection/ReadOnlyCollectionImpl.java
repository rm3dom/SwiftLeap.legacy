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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author ruan
 */
public class ReadOnlyCollectionImpl<E> implements ReadOnlyCollection<E> {

    Collection<? extends E> backingColl;

    public ReadOnlyCollectionImpl() {
        backingColl = new ArrayList<>(0);
    }

    public ReadOnlyCollectionImpl(Collection<? extends E> backingColl) {
        this.backingColl = backingColl;
    }

    @Override
    public int size() {
        return backingColl.size();
    }

    @Override
    public boolean isEmpty() {
        return backingColl.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingColl.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<? extends E> iter = backingColl.iterator();
        return new Iterator<E>() {

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public E next() {
                return iter.next();
            }

            @Override
            public void remove() {
                throw new ReadOnlyException("Read only collection");
            }
        };
    }

    public E first() {
        Iterator<E> iter = iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public Object[] toArray() {
        return backingColl.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return backingColl.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backingColl.containsAll(c);
    }

    @Override
    public boolean add(E e) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public boolean remove(Object o) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public void clear() {
        throw new ReadOnlyException("Read only collection");
    }
}
