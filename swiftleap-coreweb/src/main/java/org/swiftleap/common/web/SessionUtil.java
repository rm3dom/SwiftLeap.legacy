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

import org.swiftleap.common.util.StringUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionUtil {
    public static String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader("X-SessionId");
        if (!StringUtil.isNullOrWhites(sessionId))
            return sessionId;
        if (request.getCookies() == null)
            return "";
        for (Cookie c : request.getCookies()) {
            if (c.getName().equalsIgnoreCase("session")) {
                if (!StringUtil.isNullOrWhites(c.getValue()))
                    return c.getValue();
            }
        }
        return "";
    }

    public static void setSessionId(String sessionId, HttpServletResponse response) {
        if (StringUtil.isNullOrWhites(sessionId)) {
            clearSessionId(response);
            return;
        }
        //SameSite = PropertyManager.IsDevEnvironment ? SameSiteMode.None : SameSiteMode.Lax,
        //SecurePolicy = CookieSecurePolicy.SameAsRequest
        Cookie cookie = new Cookie("session", sessionId);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    public static void clearSessionId(HttpServletResponse response) {
        Cookie cookie = new Cookie("session", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
