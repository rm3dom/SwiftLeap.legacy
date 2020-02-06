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
package org.swiftleap.common.persistance.impl;

import org.springframework.stereotype.Repository;
import org.swiftleap.common.persistance.TenantedJpaDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by ruans on 2017/06/03.
 */
@Repository
public class TenantSequenceDao extends TenantedJpaDao<TenantSequenceDbo, Long> {
    @PersistenceContext(unitName = "corePersistenceUnit")
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TenantSequenceDbo findByName(String name) {
        CriteriaQuery<TenantSequenceDbo> cq = createQuery();
        Root<TenantSequenceDbo> root = cq.from(TenantSequenceDbo.class);
        return getSingleResult(cq,
                cb().equal(root.get(TenantSequenceDbo_.name), name));
    }
}
