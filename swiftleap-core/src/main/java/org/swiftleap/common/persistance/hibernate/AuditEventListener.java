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

package org.swiftleap.common.persistance.hibernate;


import org.hibernate.event.spi.*;
import org.swiftleap.common.persistance.Audited;
import org.swiftleap.common.security.SecurityContext;
import org.swiftleap.common.security.Tenanted;

import java.security.Principal;
import java.util.Objects;

/**
 * @author ruan
 */
public class AuditEventListener implements PreInsertEventListener, PreUpdateEventListener {

    private boolean processEvent(AbstractPreDatabaseOperationEvent event, Object[] state) {
        return false;
    }

    int indexOf(String[] props, String prop) {
        for (int i = 0; i < props.length; i++)
            if (Objects.equals(props[i], prop))
                return i;
        return -1;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object object = event.getEntity();
        Object[] props = event.getState();
        String[] propNames = event.getPersister().getPropertyNames();
        if (event.getEntity() instanceof Audited) {
            Principal scx = SecurityContext.getPrincipal();
            Audited.AuditFields aud = (Audited.AuditFields) props[indexOf(propNames, "auditInfo")];
            aud.created(scx);
            ((Audited) object).getAuditInfo().created(scx);
        }
        //Dont set the tenant on update
        if (object instanceof Tenanted) {
            Integer tenantId = SecurityContext.getTenantId();
            Tenanted tenanted = (Tenanted) object;
            tenanted.setTenantId(tenantId);
            int index = indexOf(propNames, "tenantId");
            if(index > -1)
                props[index] = tenantId;
        }
        return processEvent(event, event.getState());
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object object = event.getEntity();
        Object[] props = event.getState();
        String[] propNames = event.getPersister().getPropertyNames();

        if (object instanceof Audited) {
            Principal scx = SecurityContext.getPrincipal();
            Audited.AuditFields aud = (Audited.AuditFields) props[indexOf(propNames, "auditInfo")];
            aud.updated(scx);
            ((Audited) object).getAuditInfo().updated(scx);
        }
        if (object instanceof Tenanted) {
            Integer tenantId = SecurityContext.getTenantId();
            Tenanted tenanted = (Tenanted) object;
            if (!Objects.equals(tenantId, tenanted.getTenantId()))
                throw new RuntimeException("Tenant id may not be changed, try impersonating the tenant instead.");

        }
        return processEvent(event, event.getState());
    }
}
