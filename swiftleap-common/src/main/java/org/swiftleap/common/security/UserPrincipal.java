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
import org.swiftleap.common.util.StringUtil;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
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
        public Collection<? extends SecRoleCode> getPrincipalRoles() {
            return new ArrayList<>(0);
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

    Long getPartyId();

    /**
     * Returns true if the user is managed by this system.
     * @return
     */
    default boolean isManaged() {
        return false;
    }

    default boolean isGuest() {
        return (StringUtil.isNullOrWhites(getUserName()) || getUserName().equalsIgnoreCase("guest"));
    }

    default  boolean isSuper() {
        return (getUserName() != null
                && getUserName().equalsIgnoreCase("system"))
                || (getPrincipalRoles().stream().anyMatch(
                        r -> r.getCode().equalsIgnoreCase("system")
                        || r.getCode().equalsIgnoreCase("sysadm")
                        || r.getCode().equalsIgnoreCase("admin")));
    }

    /**
     * All active roles, including delegates.
     *
     * @return
     */
    Collection<? extends SecRoleCode> getPrincipalRoles();

    default boolean hasAnyActiveRole(Iterable<String> roles) {
        if (roles == null) {
            return false;
        }
        for (String sr : roles) {
            if (sr.equalsIgnoreCase("user") && !isGuest())
                return true;
            if (sr.equalsIgnoreCase("guest") && isGuest())
                return true;
            if ((!sr.equalsIgnoreCase("guest")) && isSuper())
                return true;
            for (SecRoleCode r : getPrincipalRoles()) {
                if (sr.equalsIgnoreCase(r.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean hasActiveRole(SecRoleCode role) {
        if (role == null || role.getCode() == null) {
            return false;
        }

        if (Objects.equals(role.getCode(), "user") && !isGuest())
            return true;
        if (Objects.equals(role.getCode(), "guest") && isGuest())
            return true;
        if ((!Objects.equals(role.getCode(), "guest")) && isSuper())
            return true;

        for (SecRoleCode r : getPrincipalRoles()) {
            if (Objects.equals(role.getCode(), r.getCode())) {
                return true;
            }
        }
        return false;
    }

    default boolean hasAnyActiveRole(SecRoleCode... roles) {
        for (SecRoleCode r : roles) {
            if (hasActiveRole(r)) {
                return true;
            }
        }
        return false;
    }
}