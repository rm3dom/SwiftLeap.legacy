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


import org.swiftleap.common.collection.ManagedCollectionImpl;
import org.swiftleap.common.security.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author ruan
 */
abstract class AbstractSecurityRolesCollection<E extends SecRole> extends ManagedCollectionImpl<E> implements SecurityRolesCollection<E> {

    public AbstractSecurityRolesCollection(Collection<E> backingColl, Object owner) {
        super(backingColl, owner);
    }

    @Override
    public E findByCode(String code) {
        for (E e : this) {
            if (e.getCode().equalsIgnoreCase(code)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public ReadSecurityRolesCollection<E> findByActive() {
        List<E> ret = new ArrayList<>();
        for (E r : this) {
            if (r.isActive()) {
                ret.add(r);
            }
        }
        return new ReadSecurityRolesCollectionImpl<>(ret, getOwner());
    }

    @Override
    public boolean add(String code) {
        SecurityService sc = SecurityContext.getSecurityService();
        SecRole role = sc.getSecurityRole(code);
        return add((E) role);
    }

    @Override
    public boolean addAll(String... codes) {
        if (codes == null)
            return true;
        for (String c : codes) {
            add(c);
        }
        return true;
    }

    @Override
    public void setRoles(String... codes) {
        for (String s : codes) {
            if (!hasRole(s))
                add(s);
        }
        retainCodes(codes);
    }

    @Override
    public boolean hasRole(String code) {
        if (code == null)
            return false;
        for (E e : this) {
            if (e.getCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAnyRole(String... codes) {
        if (codes == null)
            return false;
        for (String c : codes) {
            if (hasRole(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasRole(SecRole role) {
        return hasAnyRole(role.getCode());
    }

    @Override
    public boolean hasAnyRole(SecRole... roles) {
        for (SecRole r : roles) {
            if (hasRole(r)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean retainCodes(String... codes) {
        if (codes == null)
            return true;
        Arrays.sort(codes);
        Iterator<E> iter = iterator();
        while (iter.hasNext()) {
            E e = iter.next();
            if (Arrays.binarySearch(codes, e.getCode()) < 0) {
                iter.remove();
            }
        }
        return true;
    }

    @Override
    public Set<SecRole> flatten(Predicate<SecRole> pred) {
        return flatten(new HashSet<>(), pred);
    }

    @Override
    public Set<SecRole> flatten(Set<SecRole> set, Predicate<SecRole> pred) {
        forEach(e -> {
            if (!set.contains(e) && pred.test(e)) {
                set.add(e);
                set.addAll(e.getDelegates().flatten(set, pred));
            }
        });
        return set;
    }
}
