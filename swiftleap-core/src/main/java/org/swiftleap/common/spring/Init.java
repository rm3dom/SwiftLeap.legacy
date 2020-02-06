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
package org.swiftleap.common.spring;

import lombok.val;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.swiftleap.cms.CmsService;
import org.swiftleap.common.config.PropKeys;
import org.swiftleap.common.persistance.hibernate.AuditEventListener;
import org.swiftleap.common.security.*;
import org.swiftleap.common.security.impl.SecurityAppCtx;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.persistence.EntityManagerFactory;
import java.util.Collection;

@Component("CoreInit")
public class Init {
    private static final Logger log = LoggerFactory.getLogger(Init.class);
    @Autowired
    Collection<EntityManagerFactory> entityManagers;
    @Autowired
    SecurityService securityService;
    @Autowired
    EncryptionController encryptionController;
    @Autowired(required = false)
    CmsService cmsService;
    TransactionTemplate txTemplate;

    @Value(value = PropKeys._SECURITY_SSL_TRUSTALL)
    private boolean sslTrustAll = true;
    @Value(value = PropKeys._SECURITY_ADMIN_USERNAME)
    private String adminUserName = "sysadm";
    @Value(value = PropKeys._SECURITY_ADMIN_PASSWORD)
    private String adminPassword = "5y5adm3tt3r";
    @Value(value = PropKeys._SECURITY_ADMIN_EMAIL)
    private String adminEmail = "rm3dom@gmail.com";

    @Autowired
    public void setTxTemplate(PlatformTransactionManager txManager) {
        this.txTemplate = new TransactionTemplate(txManager);
    }

    @PostConstruct
    public void init() {
        initTrustAll();

        log.info("Config: Admin User: " + adminUserName);
        log.info("Config: Admin Email: " + adminEmail);

        txTemplate.execute((status) -> {
            initAuditEventListener();
            return true;
        });


        txTemplate.execute((status) -> {
            initSecurity();
            return true;
        });

    }

    private void initTrustAll() {
        if (!sslTrustAll)
            return;
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }


    private void initAuditEventListener() {
        entityManagers.forEach(em -> {
            SessionFactoryImpl sessionFactory = em.unwrap(SessionFactoryImpl.class);
            AuditEventListener listener = new AuditEventListener();
            EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(
                    EventListenerRegistry.class);
            registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(listener);
            registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(listener);
        });
    }

    private void initSecurity() {
        SecurityAppCtx.setCmsService(cmsService);
        SecurityAppCtx.setSecurityService(securityService);
        SecurityAppCtx.setEncryptionController(encryptionController);

        Tenant systemTenant = securityService.getTenant(0);
        if (systemTenant == null) {
            systemTenant = securityService.createTenant(TenantRequest.builder()
                    .id(0)
                    .name("system")
                    .activated(true)
                    .longName("system")
                    .countryCode(SecurityContext.getCountryCode())
                    .fqdn("localhost")
                    .build());
        } else {
            systemTenant = securityService.updateTenant(TenantRequest.builder()
                    .id(0)
                    .name("system")
                    .activated(true)
                    .longName("system")
                    .countryCode(SecurityContext.getCountryCode())
                    .fqdn("localhost")
                    .build());
        }

        SecRole userRole = securityService.getSecurityRole("user");
        if (userRole == null)
            userRole = securityService.createSecurityRole(
                    "user",
                    "user",
                    "Any authenticated user",
                    SecRole.Status.ACTIVE);

        SecRole guestRole = securityService.getSecurityRole("guest");
        if (guestRole == null)
            guestRole = securityService.createSecurityRole(
                    "guest",
                    "guest",
                    "Not authenticated",
                    SecRole.Status.ACTIVE);

        SecRole sysadmRole = securityService.getSecurityRole("sysadm");
        if (sysadmRole == null) {
            sysadmRole = securityService.createSecurityRole(
                    "sysadm",
                    "sysadm",
                    "Super User",
                    SecRole.Status.ACTIVE);

            sysadmRole.getDelegates().add(userRole);
        }


        User systemUser;
        User sysadmUser;

        try {
            SecurityContext.push(SecurityContext.createContext(
                    () -> "system",
                    systemTenant,
                    "sysadm"));

            systemUser = securityService.findUserByCred("system", null, null);
            if (systemUser == null) {
                val roles = new String[]{sysadmRole.getCode()};

                systemUser = securityService.createUser(
                        UserRequest.builder()
                                .userName("system")
                                .description("System")
                                .password("5y5adm3tt3r")
                                .activated(true)
                                .roles(roles)
                                .build());
            }

            sysadmUser = securityService.findUserByCred(adminUserName, null, null);
            if (sysadmUser == null) {
                val roles = new String[]{sysadmRole.getCode()};

                sysadmUser = securityService.createUser(
                        UserRequest.builder()
                                .userName(adminUserName)
                                .description("Super User")
                                .email(adminEmail)
                                .password(Obs.decryptIfObs(adminPassword))
                                .activated(true)
                                .roles(roles)
                                .build());
            }
        } finally {
            SecurityContext.pop();
        }
    }
}
