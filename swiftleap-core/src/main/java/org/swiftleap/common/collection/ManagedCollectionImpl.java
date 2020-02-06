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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.swiftleap.common.persistance.GenericEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author ruan
 */
public class ManagedCollectionImpl<E extends GenericEntity<?>> implements ManagedCollection<E> {

    private static Log log = LogFactory.getLog(ManagedCollectionImpl.class);
    ImplementationConverter<E> converter = new ImplementationConverter<E>() {
        @Override
        public E convert(E other) {
            return other;
        }
    };
    private Collection<E> backingColl = null;
    private Object owner;

    public ManagedCollectionImpl(Collection<E> backingColl, Object owner) {
        this.backingColl = backingColl;
        this.owner = owner;
    }

    public ManagedCollectionImpl(Collection<E> backingColl, Object owner, ImplementationConverter<E> converter) {
        this.backingColl = backingColl;
        this.owner = owner;
        this.converter = converter;
    }

    protected final Object getOwner() {
        return owner;
    }

    protected final Collection<E> getBackingCollection() {
        return backingColl;
    }

    @Override
    public final E findById(Object id) {
        for (E e : this) {
            if (id == e.getId() || id.equals(e.getId())) {
                return e;
            }
        }
        return null;
    }

    @Override
    public final int size() {
        if (backingColl == null) {
            return 0;
        }
        return backingColl.size();
    }

    @Override
    public final boolean isEmpty() {
        return backingColl == null || backingColl.isEmpty();
    }

    @Override
    public final boolean contains(Object o) {
        if (backingColl == null) {
            return false;
        }
        return backingColl.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        if (backingColl == null) {
            return new Iterator<E>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public E next() {
                    return null;
                }

                @Override
                public void remove() {
                }
            };
        }
        final Iterator<E> iter = backingColl.iterator();
        return new Iterator<E>() {
            E e;

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public E next() {
                e = iter.next();
                return e;
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    @Override
    public final Object[] toArray() {
        if (backingColl == null) {
            return new Object[]{};
        }
        return backingColl.toArray();
    }

    @Override
    public final <T> T[] toArray(T[] a) {
        if (backingColl == null) {
            return new ArrayList<T>().toArray(a);
        }
        return backingColl.toArray(a);
    }

    @Override
    public final E first() {
        Iterator<E> iter = iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public E addGet(E e) {
        e = converter.convert(e);

        if (backingColl == null) {
            return null;
        }

        backingColl.add(e);

        return e;
    }

    @Override
    public final boolean add(E e) {

        E ret = addGet(e);

        return ret != null;
    }

    @Override
    public boolean remove(Object o) {
        if (backingColl == null) {
            return false;
        }

        backingColl.remove(o);

        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (backingColl == null) {
            return false;
        }
        return backingColl.containsAll(c);
    }

    @Override
    public final boolean addAll(E... c) {
        for (E e : c) {
            add(e);
        }
        return true;
    }

    @Override
    public final boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            add(e);
        }
        return true;
    }

    @Override
    public final boolean removeAll(E... a) {
        for (Object e : a) {
            remove(e);
        }
        return true;
    }

    @Override
    public final boolean removeAll(Collection<?> c) {
        for (Object e : c) {
            remove(e);
        }
        return true;
    }

    @Override
    public final boolean retainAll(Collection<?> c) {
        Iterator<E> iter = iterator();
        while (iter.hasNext()) {
            E e = iter.next();
            if (!c.contains(e)) {
                iter.remove();
            }
        }
        return true;
    }

    @Override
    public final void clear() {
        Iterator<E> iter = iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            iter.remove();
        }
    }
}
