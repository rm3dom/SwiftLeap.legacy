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
package org.swiftleap.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.swiftleap.common.security.*;
import org.swiftleap.common.util.Base64;
import org.swiftleap.common.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * Created by ruans on 2017/06/12.
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {
    @Autowired
    AuthenticationService securityService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext.clear();
        String host = request.getHeader("Host");
        String reqTenantId = request.getHeader("X-TenantId");
        String auth = request.getHeader("Authorization");
        String authType = request.getAuthType();
        String sessionId = SessionUtil.getSessionId(request);
        SecurityContext scx = null;
        Tenant tenant = null;
        Principal user = null;
        String tenantId = null;

        //If we have a user, use its tenant id
        Session session = securityService.getSession(sessionId);
        if (session != null && session.getUser().getTenantId() != null) {
            tenantId = session.getUser().getTenantId().toString();
        }

        //Else use the one explicitly passed in header
        if (StringUtil.isNullOrWhites(tenantId)) {
            tenantId = reqTenantId;
        }

        //Get the tenant
        if (!StringUtil.isNullOrWhites(tenantId)) {
            tenant = securityService.getTenant(Integer.parseInt(tenantId));
            if (tenant == null)
                throw new SecurityException("Invalid Tenant: " + tenantId);
        } else if (host != null && !host.isEmpty())
            tenant = securityService.getTenantByFqdn(host);

        if (tenant == null)
            tenant = securityService.getTenant(SecurityContext.DEFAULT_TENANT_ID);


        if (!StringUtil.isNullOrWhites(auth)
                && auth.toLowerCase().startsWith("basic")) {
            //TODO unescape HTML chars.
            auth = auth.replaceAll("(?i)basic\\s*", "");
            String[] parts = Base64.decodeString(auth).split("[:]");
            session = SecurityContext.doImpersonation(tenant,
                    () -> {
                        try {
                            return securityService.loginApi(parts.length > 0 ? parts[0] : "", parts.length > 1 ? parts[1] : "");
                        } catch (ManagedSecurityException e) {
                            throw new SecurityException(e);
                        }
                    });
        }

        //Get the user
        if (session != null)
            user = session.getUser();
        else
            user = () -> "guest";

        //Create the context
        scx = SecurityContext.createContext(user, tenant, "guest");
        SecurityContext.push(scx);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityContext.pop();
    }
}
