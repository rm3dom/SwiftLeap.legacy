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

import org.swiftleap.common.types.Range;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/06/04.
 */
public interface SecurityService {
    Tenant createTenant(TenantRequest request);

    Tenant getTenant(Integer tenantId);

    Stream<? extends Tenant> findTenants();

    Stream<? extends SecRole> getSecurityRoles();

    SecRole getSecurityRole(String code);

    SecRole getSecurityRole(Long roleId);

    void deleteSecurityRole(SecRole role);

    SecRole createSecurityRole(String code, String name, String description, SecRole.Status status);

    SecRole createSecurityDelegate(SecRole delegatingRole, SecRole delegateTo);

    SecRole createSecurityRole(User user, SecRole secRole);

    Collection<SecRole> createSecurityRole(User user, SecRole... secRoles);

    User createUser(UserRequest user);

    User updateUser(UserRequest user);

    boolean deleteUser(User user);

    Stream<? extends User> findUsersByParty(Long partyId);

    User getUser(Long userId);

    User findUserByCred(String userName, String email, String password);

    Session login(String userName, String password) throws ManagedSecurityException;

    Session loginApi(String userName, String password) throws ManagedSecurityException;

    Session getSession(String sessionId);

    Stream<? extends User> findByRoles(String[] roles);

    Stream<? extends User> find(String any, User.UserStatus status, Range range);

    Tenant getTenantByFqdn(String host);

    Tenant updateTenant(TenantRequest tenant);

    void deleteSession(String sessionId);
}
