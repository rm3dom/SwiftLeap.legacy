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


import org.swiftleap.common.collection.ReadOnlyCollection;
import org.swiftleap.common.collection.ReadOnlyCollectionImpl;

import java.security.Principal;
import java.util.Objects;

/**
 * Pointless.
 *
 * @author ruan
 */
public interface UserPrincipal extends Principal, Tenanted {

    UserPrincipal Guest = new UserPrincipal() {
        @Override
        public String getDescription() {
            return "Guest";
        }

        @Override
        public String getUserName() {
            return null;
        }

        @Override
        public Long getUserId() {
            return 0L;
        }

        @Override
        public Long getPartyId() {
            return 0L;
        }

        @Override
        public boolean isGuest() {
            return true;
        }

        @Override
        public boolean isSuper() {
            return false;
        }

        @Override
        public Integer getTenantId() {
            return null;
        }

        @Override
        public void setTenantId(Integer tenantId) {

        }

        @Override
        public ReadOnlyCollection<? extends SecRoleIdentifier> getPrincipalRoles() {
            return new ReadOnlyCollectionImpl<>();
        }

        @Override
        public boolean hasAnyActiveRole(Iterable<String> roles) {
            for (String r : roles) {
                if (r.equalsIgnoreCase("guest"))
                    return true;
            }
            return false;
        }

        @Override
        public String getName() {
            return "guest";
        }
    };

    /**
     * A description of the user, such as "Name Surname". Same as toString()
     *
     * @return user description.
     */
    String getDescription();

    /**
     * May or may not be the same as name().
     *
     * @return
     */
    String getUserName();

    /**
     * May or may not be the same as name().
     *
     * @return
     */
    Long getUserId();

    Long getPartyId();

    boolean isGuest();

    boolean isSuper();

    /**
     * All active roles, including delegates.
     *
     * @return
     */
    ReadOnlyCollection<? extends SecRoleIdentifier> getPrincipalRoles();

    default boolean hasAnyActiveRole(Iterable<String> roles) {
        if (roles == null) {
            return false;
        }
        for (SecRoleIdentifier r : getPrincipalRoles()) {
            for (String sr : roles) {
                if (sr.equalsIgnoreCase(r.getCode())) {
                    return true;
                }
                if (sr.equalsIgnoreCase("user") && !isGuest())
                    return true;
                if (sr.equalsIgnoreCase("guest") && isGuest())
                    return true;
                if ((!sr.equalsIgnoreCase("guest")) && isSuper())
                    return true;
            }
        }
        return false;
    }

    default boolean hasActiveRole(SecRoleIdentifier role) {
        if (role == null) {
            return false;
        }

        for (SecRoleIdentifier r : getPrincipalRoles()) {
            if (Objects.equals(role.getCode(), r.getCode())) {
                return true;
            }
            if (Objects.equals(role.getCode(), "user") && !isGuest())
                return true;
            if (Objects.equals(role.getCode(), "guest") && isGuest())
                return true;
            if ((!Objects.equals(role.getCode(), "guest")) && isSuper())
                return true;
        }
        return false;
    }

    default boolean hasAnyActiveRole(SecRoleIdentifier... roles) {
        for (SecRoleIdentifier r : roles) {
            if (hasActiveRole(r)) {
                return true;
            }
        }
        return false;
    }
}