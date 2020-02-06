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
package org.swiftleap.common.config.impl.model;

import org.springframework.stereotype.Repository;
import org.swiftleap.common.persistance.GenericJpaDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/06/08.
 */
@Repository
public class ConfigDao extends GenericJpaDao<ConfigDbo, ConfigDbo.ConfigId> {
    @PersistenceContext(unitName = "corePersistenceUnit")
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Stream<ConfigDbo> findAll(String section) {
        CriteriaQuery<ConfigDbo> config = createQuery();
        Root<ConfigDbo> root = config.from(ConfigDbo.class);
        return getResultList(config, cb().equal(root.get("id.section"), section));
    }
}

