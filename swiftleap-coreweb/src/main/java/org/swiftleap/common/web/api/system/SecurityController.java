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
package org.swiftleap.common.web.api.system;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.swiftleap.common.security.SecurityService;
import org.swiftleap.common.security.UserPrincipal;
import org.swiftleap.common.security.UserRequest;
import org.swiftleap.common.security.dto.UserDto;
import org.swiftleap.common.service.ManagedServiceException;
import org.swiftleap.common.util.StringUtil;
import org.swiftleap.common.util.dto.PairDto;
import org.swiftleap.common.web.SessionUtil;
import org.swiftleap.common.web.api.system.model.AuthRequestDto;
import org.swiftleap.common.web.api.system.model.SearchUsersRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by ruans on 2017/07/29.
 */
@RequestMapping("/api/v1/security")
@RestController
public class SecurityController {
    @Autowired
    SecurityService securityService;


    UserDto mapUser(UserPrincipal user, String sessionId) {
        return new UserDto(user, sessionId);
    }

    @RequestMapping(value = "sessions", method = RequestMethod.GET)
    public UserDto getUser(HttpServletRequest request) {
        String sessionId = SessionUtil.getSessionId(request);
        if (StringUtil.isNullOrWhites(sessionId))
            return null;
        val session = securityService.getSession(sessionId);
        if (session == null)
            return null;
        return mapUser(session.getUser(), session.getSessionId());
    }

    @Transactional(readOnly = false, noRollbackFor = {SecurityException.class})
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public UserDto login(@RequestBody AuthRequestDto auth, HttpServletResponse response) throws ManagedServiceException {
        val session = securityService.login(auth.getUserName(), auth.getPassword());
        if (session == null)
            return null;
        SessionUtil.setSessionId(session.getSessionId(), response);
        return mapUser(session.getUser(), session.getSessionId());
    }

    @Transactional(readOnly = false, noRollbackFor = {SecurityException.class})
    @RequestMapping(value = "logout/{sessionId}", method = RequestMethod.PUT)
    public void logout(@PathVariable String sessionId, HttpServletResponse response) {
        securityService.deleteSession(sessionId);
        SessionUtil.clearSessionId(response);
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "users", method = RequestMethod.POST)
    public Collection<UserDto> users(@RequestBody SearchUsersRequestDto search) {
        return securityService
                .find(search.getFilter(), search.getStatus(), search.getRange(50))
                .map(u -> mapUser(u, ""))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "roles", method = RequestMethod.GET)
    public Collection<PairDto> roles() {
        return securityService
                .getSecurityRoles()
                .filter(r -> !r.getCode().equalsIgnoreCase("guest")
                        && !r.getCode().equalsIgnoreCase("user"))
                .map(r -> new PairDto(r.getDescription(), r.getCode()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    @RequestMapping(value = "user", method = RequestMethod.POST)
    public UserDto createUser(@RequestBody UserRequest userRequest) {
        if (userRequest.getId() == null) {
            return mapUser(securityService.createUser(userRequest), "");
        } else {
            return mapUser(securityService.updateUser(userRequest), "");
        }
    }
}
