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

package org.swiftleap.common.security.impl;


import org.swiftleap.common.collection.ReadOnlyException;
import org.swiftleap.common.security.ReadSecurityRolesCollection;
import org.swiftleap.common.security.SecRole;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author ruan
 */
public class ReadSecurityRolesCollectionImpl<E extends SecRole> extends AbstractSecurityRolesCollection<E> implements ReadSecurityRolesCollection<E> {

    public ReadSecurityRolesCollectionImpl(Collection<E> backingColl, Object owner) {
        super(backingColl, owner);
    }

    @Override
    public boolean add(String code) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public boolean addAll(String... codes) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public E addGet(E e) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public boolean remove(Object o) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public boolean retainCodes(String... codes) {
        throw new ReadOnlyException("Read only collection");
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<E> iter = super.iterator();
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
}
