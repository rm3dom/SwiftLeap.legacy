package org.swiftleap.common.security;

import java.util.Map;

public interface AuthenticationService {
    Tenant getTenant(Integer tenantId);

    Tenant getTenantByFqdn(String host);

    Session login(String userName, String password, Map<String, Object> opts) throws ManagedSecurityException;

    Session loginApi(String userName, String password, Map<String, Object> opts) throws ManagedSecurityException;

    Session loginApi(String apiKey, Map<String, Object> opts) throws ManagedSecurityException;
}
