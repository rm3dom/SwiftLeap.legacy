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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.swiftleap.common.security.SecurityService;
import org.swiftleap.common.security.Tenant;
import org.swiftleap.common.security.TenantRequest;
import org.swiftleap.common.types.NameSearchRequest;
import org.swiftleap.common.web.api.system.model.NodeInfoDto;
import org.swiftleap.common.web.api.system.model.TenantDto;
import org.swiftleap.common.web.api.system.model.UpdateInfoDto;
import org.swiftleap.update.UpdateService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by ruans on 2017/08/05.
 */
@RequestMapping("/api/v1/system")
@RestController
public class SystemController {
    @Autowired
    SecurityService securityService;

    @Autowired
    UpdateService updateService;

    @Transactional(readOnly = true)
    @RequestMapping(value = "tenants", method = RequestMethod.POST)
    public Collection<TenantDto> tenants(@RequestBody NameSearchRequest search) {
        return securityService
                .findTenants()
                .filter(t -> t.getName().contains(search.getName()))
                .skip(search.getStart())
                .limit(search.getMaxResults(100))
                .sorted(Comparator.comparing(Tenant::getName))
                .map(TenantDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequestMapping(value = "savetenant", method = RequestMethod.POST)
    public TenantDto createTenant(@RequestBody TenantRequest tenant) {
        if (tenant.getId() == null)
            return new TenantDto(securityService.createTenant(tenant));
        else
            return new TenantDto(securityService.updateTenant(tenant));
    }


    @Transactional
    @RequestMapping(value = "updatetenant", method = RequestMethod.POST)
    public TenantDto updateTenant(@RequestBody TenantRequest tenant) {
        return new TenantDto(securityService.updateTenant(tenant));
    }


    @RequestMapping(value = "restart", method = RequestMethod.GET)
    public void restart() {
        updateService.restart();
    }

    @RequestMapping(value = "update", method = RequestMethod.GET)
    public void update() {
        updateService.update(msg -> {
        });
        updateService.restart();
    }

    @RequestMapping(value = "update/info", method = RequestMethod.GET)
    public UpdateInfoDto updateInfo() {
        UpdateInfoDto ret = new UpdateInfoDto();
        ret.setCurrentVersion(updateService.getCurrentVersion().toString());
        ret.setPatchedVersion(updateService.getPatchedVersion().toString());
        ret.setLatestVersion(updateService.getLatestVersion().toString());
        ret.setRestartRequired(updateService.isRestartRequired());
        ret.setOnLatest(updateService.getCurrentVersion().compareTo(updateService.getLatestVersion()) >= 0);
        return ret;
    }

    @RequestMapping(value = "update/node", method = RequestMethod.POST)
    public void reportInstance(@RequestBody NodeInfoDto node, HttpServletRequest request) {
        node.setRemoteIp(request.getRemoteAddr());
        updateService.logNodeInfo(node);
    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    public void ping() {
    }
}
