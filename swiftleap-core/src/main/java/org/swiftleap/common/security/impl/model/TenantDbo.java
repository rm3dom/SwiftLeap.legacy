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
package org.swiftleap.common.security.impl.model;

import lombok.Getter;
import lombok.Setter;
import org.swiftleap.common.codegen.anotate.CGIgnore;
import org.swiftleap.common.config.Config;
import org.swiftleap.common.config.ConfigServiceFactory;
import org.swiftleap.common.persistance.GenericEntity;
import org.swiftleap.common.security.Tenant;
import org.swiftleap.common.util.Singleton;

import javax.persistence.*;

/**
 * Created by ruans on 2017/06/08.
 */
@Entity
@Table(name = "sec_tenant", uniqueConstraints = {
        @UniqueConstraint(name = "UK_sec_tenant_fqd", columnNames = "fqdn")
})
@CGIgnore
@Getter
@Setter
public class TenantDbo implements Tenant, GenericEntity<Integer> {
    @Id
    Integer id;
    @Column(name = "party_id")
    Long partyId;
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "country_code")
    String countryCode;
    @Column(name = "fqdn")
    String fqdn;
    @Column(name = "status", nullable = false)
    TenantStatus status = TenantStatus.ACTIVE;

    @Transient
    private
    Singleton<Config> config = new Singleton<>(() -> ConfigServiceFactory
            .getInstance()
            .getConfig("tenant:" + id));

    @Override
    public Config getConfig() {
        return config.get();
    }

    @Override
    public Integer getTenantId() {
        return getId();
    }

    @Override
    public void setTenantId(Integer tenantId) {
        id = tenantId;
    }
}
