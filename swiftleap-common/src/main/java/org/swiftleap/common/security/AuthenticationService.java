package org.swiftleap.common.security;

public interface AuthenticationService {
    Session getSession(String sessionId);

    Tenant getTenant(Integer tenantId);

    Tenant getTenantByFqdn(String host);

    Session login(String userName, String password) throws ManagedSecurityException;

    Session loginApi(String userName, String password) throws ManagedSecurityException;
}
