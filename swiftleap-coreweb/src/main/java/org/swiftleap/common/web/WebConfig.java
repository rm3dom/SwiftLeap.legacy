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

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.swiftleap.common.config.PropKeys;
import org.swiftleap.common.util.StringUtil;

@Component
@Getter
@Setter
public class WebConfig {
    @Value(value = PropKeys._WEB_RESOURCE_URL)
    String webResourceUrl;
    @Value(value = PropKeys._WEB_SITE_NAME)
    String webSiteName;
    @Value(value = PropKeys._SECURITY_GLOBAL_USERS)
    boolean globalUsers;
    @Value(value = PropKeys._COMPANY_NAME)
    String companyName;
    @Value(value = PropKeys._COMPANY_WEBSITE_URL)
    String companyWebSiteUrl;
    @Value(value = PropKeys._WEB_SITE_THEME)
    String themeName;
    String versionHash = "36252675";
    String latestVersion;
    String currentVersion;
    boolean updatesAvailable;
    @Value(value = PropKeys._WEB_SITE_ICON)
    String webSiteIconUrl = "img/ruleslogo.png";

    public WebConfig() {
    }

    public WebConfig(WebConfig other) {
        webResourceUrl = other.webResourceUrl;
        webSiteName = other.webSiteName;
        globalUsers = other.globalUsers;
        companyName = other.companyName;
        companyWebSiteUrl = other.companyWebSiteUrl;
        themeName = other.themeName;
        versionHash = other.versionHash;
        webSiteIconUrl = other.webSiteIconUrl;
        currentVersion = other.currentVersion;
        latestVersion = other.latestVersion;
    }

    public String getWebResourceUrl() {
        if (!StringUtil.isNullOrWhites(webResourceUrl))
            return StringUtil.trimr(webResourceUrl, '/') + "/";
        return webResourceUrl;
    }

    public String getSiteName() {
        return getWebSiteName();
    }

    public String getResourceUrl() {
        return getWebResourceUrl();
    }
}
