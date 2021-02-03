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


import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swiftleap.common.service.ExecutionContext;
import org.swiftleap.common.config.Config;
import org.swiftleap.common.security.dto.UserDto;
import org.swiftleap.common.security.impl.SecurityAppCtx;
import org.swiftleap.common.service.ServiceException;

import java.security.Principal;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.swiftleap.common.security.SecurityConstant.COUNTRY_CODE;
import static org.swiftleap.common.security.SecurityConstant.TENANT_ID;


/**
 * Keeps the security context on thread local.
 * <p>
 * There must be some interceptor on some interface that must set the initial context.
 * </p>
 * <p>
 * Allows for impersonation by keeping a stack.
 * <code>
 * SecurityContext.push(SecurityContext.createContext(...));
 * try {
 * <p>
 * } finally {
 * SecurityContext.pop();
 * }
 * </code>
 * </p>
 * <p>
 * Created by ruans on 2016/09/17.
 */
public class SecurityContext {
    static final Logger log = LoggerFactory.getLogger(SecurityContext.class);
    private static final ThreadLocal<Deque<SecurityContext>> tl = ThreadLocal.withInitial(ArrayDeque<SecurityContext>::new);
    public static String DEFAULT_COUNTRY_CODE = "AU";
    public static Integer DEFAULT_TENANT_ID = 0;

    static {
        String tid = System.getenv(TENANT_ID);
        if (tid == null) {
            tid = System.getProperty(TENANT_ID);
        }
        if (tid != null) {
            DEFAULT_TENANT_ID = Integer.parseInt(tid);
        }
        if (DEFAULT_TENANT_ID == null)
            DEFAULT_TENANT_ID = 0;

        String cc = System.getenv(COUNTRY_CODE);
        if (cc == null) {
            cc = System.getProperty(COUNTRY_CODE);
        }
        if (cc != null) {
            DEFAULT_COUNTRY_CODE = cc;
        }
        if (DEFAULT_COUNTRY_CODE == null)
            DEFAULT_COUNTRY_CODE = "AU";
    }

    Tenant tenant;
    String authToken;
    Principal principal;
    String[] roles;
    String channel = "system";

    public static SecurityService getSecurityService() {
        return SecurityAppCtx.getSecurityService();
    }

    public static EncryptionController getEncryptionController() {
        return SecurityAppCtx.getEncryptionController();
    }

    public static SecurityContext getTopContext() {
        Deque<SecurityContext> deque = tl.get();
        if (deque.isEmpty()) return createSystemContext();
        return deque.peekFirst();
    }

    public static String getCountryCode() {
        Tenant t = getTenant();
        if (t == null)
            return DEFAULT_COUNTRY_CODE;
        return t.getCountryCode();
    }

    public static Principal getPrincipal() {
        SecurityContext sc = getTopContext();
        if (sc == null)
            return getGuestUser(getUserRoleNames());
        return sc.principal;
    }

    public static String getChannel() {
        SecurityContext sc = getTopContext();
        if (sc == null)
            return "system";
        return sc.channel;
    }

    public static String[] getUserRoleNames() {
        SecurityContext sc = getTopContext();
        if (sc == null)
            return new String[]{};
        return sc.roles;
    }

    public static <T> T doImpersonation(Integer tenantId, Supplier<T> runnable) {
        Tenant t = getSecurityService().getTenant(tenantId);
        return doImpersonation(t, runnable);
    }

    public static <T> T doImpersonation(Tenant t, Supplier<T> runnable) {
        if (t == null)
            throw new SecurityException("Invalid tenant");
        try (val ignored = impersonate(t)) {
            return runnable.get();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error while impersonating", ex);
            throw new ServiceException(ex);
        }
    }

    public static <T> T doImpersonation(SecurityContext t, Supplier<T> runnable) {
        if (t == null)
            throw new SecurityException("Invalid tenant");
        try (val ignored = impersonate(t)) {
            return runnable.get();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error while impersonating", ex);
            throw new ServiceException(ex);
        }
    }

    public static <T> T doImpersonation(Principal principal, Supplier<T> runnable, String... roleNames) {
        if (principal == null)
            throw new SecurityException("Invalid principal");
        try (val ignored = impersonate(principal, roleNames)) {
            return runnable.get();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error while impersonating", ex);
            throw new ServiceException(ex);
        }
    }

    public static void clear() {
        tl.get().clear();
    }

    public static void push(SecurityContext scx) {
        checkTenantImpersonationAllowed(scx.tenant);
        tl.get().push(scx);
    }

    public static void pop() {
        tl.get().pop();
    }

    public static Tenant getTenant() {
        SecurityContext sc = getTopContext();
        if (sc == null) return null;
        return sc.tenant;
    }

    public static Integer getTenantId() {
        SecurityContext sc = getTopContext();
        if (sc == null) return DEFAULT_TENANT_ID;

        Tenant ret = sc.tenant;

        return ret == null ? DEFAULT_TENANT_ID : ret.getTenantId();
    }

    public static SecurityContext createSystemContext() {
        SecurityContext ret = new SecurityContext();
        ret.tenant = getSystemTenant();
        ret.roles = new String[]{"sysadm", "user"};
        ret.channel = "system";
        ret.principal = getSystemUser();
        return ret;
    }

    public static SecurityContext createContext(String channel,
                                                Principal principal,
                                                Tenant tenant,
                                                String... roleNames) {
        SecurityContext ret = new SecurityContext();
        ret.tenant = tenant;
        ret.roles = roleNames;
        ret.channel = channel;
        ret.principal = principal == null ? getGuestUser(roleNames) : principal;
        return ret;
    }

    public static SecurityContext createContext(Principal principal,
                                                Tenant tenant,
                                                String... roleNames) {
        return createContext("system", principal, tenant, roleNames);
    }


    private static Principal getGuestUser(String... roles) {
        List<String> r = Stream.of(roles == null ? new String[]{} : roles).collect(Collectors.toList());
        return UserDto.builder()
                .activated(true)
                .roles(r)
                .userName("guest")
                .build();
    }

    private static Principal getSystemUser() {
        List<String> r = Arrays.asList("sysadm");
        return UserDto.builder()
                .activated(true)
                .roles(r)
                .userName("system")
                .build();
    }

    private static Tenant getSystemTenant() {
        return new Tenant() {
            @Override
            public Integer getTenantId() {
                return 0;
            }

            @Override
            public Config getConfig() {
                return null;
            }

            @Override
            public String getName() {
                return "system";
            }

            @Override
            public String getFqdn() {
                return "localhost";
            }

            @Override
            public String getCountryCode() {
                return DEFAULT_COUNTRY_CODE;
            }

            @Override
            public TenantStatus getStatus() {
                return TenantStatus.ACTIVE;
            }
        };
    }


    private static void checkTenantImpersonationAllowed(TenantReference tenant) {
        if (tenant == null)
            throw new ServiceException("Null tenant");
        Integer currentTenantId = getTenantId();
        if (currentTenantId != 0 && !currentTenantId.equals(tenant.getTenantId()))
            throw new SecurityException("Only tenant 0 is allowed to impersonate");
    }

    private static void checkTenantImpersonationAllowed(Principal principal) {
        if(principal instanceof Tenanted)
        {
            checkTenantImpersonationAllowed((Tenanted) principal);
            return;
        }
        Integer currentTenantId = getTenantId();
        if (currentTenantId != 0)
            throw new SecurityException("Only tenant 0 is allowed to impersonate");
    }

    private static Impersonation impersonate(Tenant tenant, Principal principal, String... roleNames) {
        checkTenantImpersonationAllowed(tenant);
        SecurityContext sc = getTopContext();
        if (sc == null)
            throw new ServiceException("Not allowed with null context");
        push(createContext(sc.channel, principal, tenant, roleNames));
        return () -> pop();
    }


    private static Impersonation impersonate(Tenant tenant) {
        checkTenantImpersonationAllowed(tenant);
        SecurityContext sc = getTopContext();
        if (sc == null)
            throw new ServiceException("Not allowed with null context");
        push(createContext(sc.channel, sc.principal, tenant, sc.roles));
        return () -> pop();
    }

    private static Impersonation impersonate(SecurityContext sc) {
        checkTenantImpersonationAllowed(sc.principal);
        push(sc);
        return () -> pop();
    }

    private static Impersonation impersonate(Principal principal, String... roleNames) {
        checkTenantImpersonationAllowed(principal);
        SecurityContext sc = getTopContext();
        if (sc == null)
            throw new ServiceException("Not allowed with null context");
        push(createContext(sc.channel, principal, sc.tenant, roleNames));
        return () -> pop();
    }
}
