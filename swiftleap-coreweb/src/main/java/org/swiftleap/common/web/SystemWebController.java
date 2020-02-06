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


import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.swiftleap.common.security.SecurityContext;
import org.swiftleap.common.security.Tenant;
import org.swiftleap.common.util.StringUtil;
import org.swiftleap.common.web.model.LoginRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SystemWebController extends AbstractUiController {

    @RequestMapping("admin.html")
    public String admin(Map<String, Object> model,
                        HttpServletRequest request, HttpServletResponse response) {
        renderSecured(model, request, response);
        return "admin";
    }

    @RequestMapping("update.html")
    public String update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("refresh") != null) {
            sendRedirect(response, "/#");
            return "update";
        }
        updateService.update(msg -> {
        });
        updateService.restart();
        return "update";
    }

    @RequestMapping("logout.html")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        sessionLogout(request, response);
    }

    private String loginResponse(Map<String, Object> model,
                                 HttpServletRequest request,
                                 LoginRequest login) {
        val tenants = securityService.findTenants()
                .filter(t -> t.getStatus() == Tenant.TenantStatus.ACTIVE)
                .collect(Collectors.toList());
        render(model, request, () -> {
        });
        model.put("login", login);
        model.put("tenants", tenants);
        return "login";
    }

    @RequestMapping("login.html")
    public String login(Map<String, Object> model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        val sessionId = SessionUtil.getSessionId(request);
        if (!StringUtil.isNullOrWhites(sessionId)) {
            securityService.deleteSession(sessionId);
            SessionUtil.clearSessionId(response);
        }

        val login = new LoginRequest();
        String redirectUrl = request.getParameter("redirect");
        login.setRedirect(redirectUrl);
        return loginResponse(model, request, login);
    }

    @RequestMapping(value = "login.html", method = RequestMethod.POST)
    public String loginPost(Map<String, Object> model,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            @ModelAttribute LoginRequest login) {
        return SecurityContext.doImpersonation(login.getTenantId(), () -> {
            try {
                val session = securityService.login(login.getName(), login.getPassword());
                if (session == null)
                    throw new SecurityException("Invalid username or password");
                SessionUtil.setSessionId(session.getSessionId(), response);

                String redirect = login.getRedirect();
                if (StringUtil.isNullOrWhites(redirect))
                    redirect = "/#";
                sendRedirect(response, redirect);
            } catch (Exception ex) {
                login.setPassword("");
                model.put("message", ex.getMessage());
                return loginResponse(model, request, login);
            }
            return loginResponse(model, request, login);
        });
    }
}
