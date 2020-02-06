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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ruan
 */
public class SecurityUtil {
    public static Set<SecRole> getAllowedDelegation(User user, boolean activeOnly) {
        return getAllowedDelegation(user.getSecurityRoles(), activeOnly);
    }

    public static Set<SecRole> getAllowedDelegation(Collection<SecRole> roles, boolean activeOnly) {
        Set<SecRole> delRoles = new HashSet<>();
        for (SecRole r : roles) {
            r.getDelegates().flatten(delRoles, e -> !activeOnly || e.isActive());
        }
        return delRoles;
    }
}
