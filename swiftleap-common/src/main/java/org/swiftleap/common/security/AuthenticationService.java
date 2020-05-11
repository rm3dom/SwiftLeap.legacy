package org.swiftleap.common.security;

import java.security.Principal;

public interface AuthenticationService {
    /**
     * Get the current user.
     * @return Current user.
     */
    Principal getPrincipal();

    Session getSession(String sessionId);

    Tenant getTenant(Integer tenantId);

    Tenant getTenantByFqdn(String host);

    Session login(String userName, String password) throws ManagedSecurityException;

    Session loginApi(String userName, String password) throws ManagedSecurityException;
}
