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
package org.swiftleap.common.config;

/**
 * System properties.
 * Note spring @Values properties start with an '_'.
 * Created by ruans on 2015/12/12.
 */
public interface PropKeys {
    //Socket options
    String _SECURITY_SSL_TRUSTALL = "${security.ssl.trustAll}";
    String _SECURITY_KEYSTORE = "${security.keystore}";
    String _SECURITY_KEYSTORE_PASSWORD = "${security.keystore.password}";
    String _SECURITY_TRUSTSTORE = "${security.truststore}";
    String _SECURITY_TRUSTSTORE_PASSWORD = "${security.truststore.password}";
    String _SECURITY_PRIVATEKEY_ALIAS = "${security.privatekey.alias}";
    String _SECURITY_PRIVATEKEY_PASSWORD = "${security.privatekey.password}";
    String _SECURITY_JWT_SIGNING_KEY = "${security.jwt.signingkey}";
    String _SECURITY_JWT_AUDIENCE = "${security.jwt.audience}";
    String _SECURITY_JWT_ISSUER = "${security.jwt.issuer}";

    //User options
    String _SECURITY_ADMIN_USERNAME = "${security.admin.username}";
    String _SECURITY_ADMIN_PASSWORD = "${security.admin.password}";
    String _SECURITY_ADMIN_EMAIL = "${security.admin.email}";
    String _SECURITY_GLOBAL_USERS = "${security.globalUsers}";

    //Ldap authentication
    String _SECURITY_LDAP_ENABLED = "${security.ldap.enabled}";
    String _SECURITY_LDAP_SSL_ENABLED = "${security.ldap.ssl.enabled}";
    String _SECURITY_LDAP_PORT = "${security.ldap.port}";
    String _SECURITY_LDAP_DNBASE = "${security.ldap.dnbase}";
    String _SECURITY_LDAP_HOST = "${security.ldap.host}";
    String _SECURITY_LDAP_USERDN = "${security.ldap.userdn}";
    String _SECURITY_LDAP_PASSWORD = "${security.ldap.password}";
    String _SECURITY_LDAP_FILTER = "${security.ldap.filter}";

    //Web config
    String _WEB_RESOURCE_URL = "${web.resourceUrl}";
    String _WEB_SITE_NAME = "${web.siteName}";
    String _WEB_SITE_ICON = "${web.siteIcon}";
    String _WEB_SITE_THEME = "${web.themeName}";
    String _COMPANY_NAME = "${company.name}";
    String _COMPANY_WEBSITE_URL = "${company.webSiteUrl}";
    String _APP_NAME = "${spring.application.name}";
    String _NODE_NAME = "${node.name}";
    String _WEB_BASE_URL = "${web.baseUrl}";

    //Update config
    String _UPDATE_REPO_URL = "${update.repoUrl}";
    String _UPDATE_NODE_INFO_URL = "${update.nodeInfoUrl}";
}
