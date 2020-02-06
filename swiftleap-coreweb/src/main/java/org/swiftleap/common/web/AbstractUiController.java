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
import org.swiftleap.common.security.SecurityContext;
import org.swiftleap.common.security.SecurityService;
import org.swiftleap.common.security.Session;
import org.swiftleap.common.security.UserPrincipal;
import org.swiftleap.common.util.StringUtil;
import org.swiftleap.ui.UiManager;
import org.swiftleap.update.UpdateService;
import org.swiftleap.update.Versioned;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class AbstractUiController {
    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UpdateService updateService;

    @Autowired
    protected UiManager uiManager;

    @Autowired
    protected WebConfig webConfig;

    protected HtmlUiRenderer renderer = new HtmlUiRenderer();

    protected void sendRedirect(HttpServletResponse response, String url) throws IOException {
        response.sendRedirect(url);
    }

    protected void sessionLogout(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = SessionUtil.getSessionId(request);

        if (!StringUtil.isNullOrWhites(sessionId))
            securityService.deleteSession(sessionId);

        SessionUtil.clearSessionId(response);

        try {
            sendRedirect(response, "/#");
        } catch (IOException e) {
        }
    }

    protected void renderSecured(Map<String, Object> model,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        render(model, request, () -> {
            try {
                String uri = request.getRequestURI();
                if (!StringUtil.isNullOrEmpty(request.getQueryString()))
                    uri += "?" + request.getQueryString();

                uri = URLEncoder.encode(uri, "UTF-8");

                sendRedirect(response, "/login.html?redirect=" + uri);
            } catch (IOException e) {
            }
        });
    }

    protected void render(Map<String, Object> model,
                          HttpServletRequest request,
                          Runnable denied) {
        String sessionId = SessionUtil.getSessionId(request);

        Session session = securityService.getSession(sessionId);

        UserPrincipal user = null;
        if (session != null)
            user = session.getUser();
        if (user == null)
            user = UserPrincipal.Guest;

        String menuHtml = renderer.render(user,
                webConfig.getSiteName(),
                "index.html#",
                webConfig.getWebSiteIconUrl(),
                uiManager.getMenuEntries());

        Versioned version = updateService.getCurrentVersion();
        Versioned latest = updateService.getLatestVersion();

        FlagsDto flags = new FlagsDto(webConfig);
        flags.setSessionId(session == null ? "" : session.getSessionId());
        flags.setTenantId(SecurityContext.getTenantId());

        flags.setCurrentVersion(version.asString());
        flags.setLatestVersion(latest.asString());
        flags.setUpdatesAvailable(latest.compareTo(version) > 0);
        flags.setVersionHash(String.valueOf(version.hashCode()));

        model.put("menuHtml", menuHtml);
        model.put("flags", flags);

        if (session == null || StringUtil.isNullOrWhites(sessionId))
            denied.run();
    }
}
