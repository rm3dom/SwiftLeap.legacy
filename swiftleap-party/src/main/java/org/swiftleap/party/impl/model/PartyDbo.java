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
package org.swiftleap.party.impl.model;

import lombok.Getter;
import lombok.Setter;
import org.swiftleap.common.config.Config;
import org.swiftleap.common.config.ConfigServiceFactory;
import org.swiftleap.common.persistance.AuditInfo;
import org.swiftleap.common.persistance.Audited;
import org.swiftleap.common.persistance.TenantedEntity;
import org.swiftleap.common.security.SecEnums;
import org.swiftleap.common.util.Singleton;
import org.swiftleap.party.Party;
import org.swiftleap.party.PartyEnums;

import javax.persistence.*;

/**
 * Created by ruans on 2017/05/02.
 */
@Entity
@Table(name = "pty_party")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public abstract class PartyDbo implements TenantedEntity<Long>, Audited<AuditInfo>, Party {
    @Getter
    @Setter
    @Column(name = "tenant_id", nullable = false)
    Integer tenantId;
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Getter
    @Setter
    @Column(name = "party_type", length = 3)
    String type;
    @Getter
    @Setter
    @Column(name = "role")
    PartyEnums.PartyRole role;
    @Getter
    @Setter
    @Column(name = "country_code", length = 3)
    String countryCode = "ZA";
    @Getter
    @Setter
    @Column(name = "reg_channel")
    SecEnums.Channel regChannel;
    @Getter
    @Setter
    @Embedded
    AuditInfo auditInfo = new AuditInfo();

    @Transient
    private Singleton<Config> config = new Singleton<>(() -> ConfigServiceFactory
            .getInstance()
            .getConfig("party:" + id));

    @Override
    public Config getConfig() {
        return config.get();
    }
}
