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


import org.swiftleap.common.collection.ManagedCollection;

import java.util.Set;
import java.util.function.Predicate;

/**
 * @author ruan
 */
public interface SecurityRolesCollection<E extends SecRole> extends ManagedCollection<E> {

    E findByCode(String code);

    ReadSecurityRolesCollection<E> findByActive();

    /**
     * Has any role including inactive ones.
     *
     * @param code
     * @return
     */
    boolean hasRole(String code);

    /**
     * Has any role including inactive ones.
     *
     * @param codes
     * @return
     */
    boolean hasAnyRole(String... codes);

    boolean add(String code);

    boolean addAll(String... code);

    void setRoles(String... code);

    /**
     * Has any role including inactive ones.
     *
     * @param role
     * @return
     */
    boolean hasRole(SecRole role);

    /**
     * Has any role including inactive ones.
     *
     * @param roles
     * @return
     */
    boolean hasAnyRole(SecRole... roles);

    boolean retainCodes(String... codes);

    Set<SecRole> flatten(Predicate<SecRole> pred);

    Set<SecRole> flatten(Set<SecRole> set, Predicate<SecRole> pred);
}
