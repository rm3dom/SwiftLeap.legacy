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

import org.springframework.stereotype.Repository;
import org.swiftleap.common.persistance.GenericJpaDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by ruans on 2017/06/04.
 */
@Repository
public class SecRoleDelegateDao extends GenericJpaDao<SecRoleDelegateDbo, Long> {
    @PersistenceContext(unitName = "corePersistenceUnit")
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
