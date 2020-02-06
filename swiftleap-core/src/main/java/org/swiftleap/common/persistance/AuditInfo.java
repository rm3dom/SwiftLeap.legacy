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
package org.swiftleap.common.persistance;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.security.Principal;
import java.util.Date;

/**
 * Created by ruans on 2017/05/22.
 */
@Getter
@Setter
@Embeddable
public class AuditInfo implements Audited.AuditFields {
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    Date creationTime;
    @Column(name = "created_by")
    String createdBy;
    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    Date lastUpdateTime;
    @Column(name = "updated_by")
    String lastUpdatedBy;

    public AuditInfo() {
    }

    public AuditInfo(Audited.AuditFields other) {
        createdBy = other.getCreatedBy();
        lastUpdatedBy = other.getLastUpdatedBy();
        creationTime = other.getCreationTime();
        lastUpdateTime = other.getLastUpdateTime();
    }

    public void copy(Audited.AuditFields o) {
        setCreatedBy(o.getCreatedBy());
        setCreatedBy(o.getCreatedBy());
        setLastUpdatedBy(o.getLastUpdatedBy());
        setLastUpdateTime(o.getLastUpdateTime());
    }


    @Override
    public void created(Principal user) {
        String userName = user == null ? "guest" : user.getName();
        creationTime = creationTime == null ? new Date() : creationTime;
        createdBy = createdBy == null ? userName : createdBy;
        updated(user);
    }

    @Override
    public void updated(Principal user) {
        String userName = user == null ? "guest" : user.getName();
        lastUpdateTime = new Date();
        lastUpdatedBy = userName;
    }
}
