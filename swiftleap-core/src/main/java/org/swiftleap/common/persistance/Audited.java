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

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;

/**
 * Created by ruans on 2016/10/13.
 */
public interface Audited<T extends Audited.AuditFields> {
    T getAuditInfo();

    interface AuditFields extends Serializable {

        Date getCreationTime();

        String getCreatedBy();

        Date getLastUpdateTime();

        String getLastUpdatedBy();

        /**
         * Update the created fields.
         *
         * @param user
         */
        void created(Principal user);

        /**
         * Update the updated fields.
         *
         * @param user
         */
        void updated(Principal user);
    }
}