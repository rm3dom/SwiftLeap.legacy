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

import org.swiftleap.common.persistance.GenericJpaDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GlobalUserDao extends GenericJpaDao<UserDbo, Long> implements UserDao {
    @PersistenceContext(unitName = "corePersistenceUnit")
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Stream<UserDbo> findUsersByParty(Long partyId) {
        CriteriaQuery<UserDbo> cq = createQuery();
        Root<UserDbo> root = cq.from(UserDbo.class);
        return getResultList(cq, cb().equal(root.get(UserDbo_.partyId), partyId));
    }

    @Override
    public UserDbo findUserByCred(String userName, String email, String encryptedPassword) {
        CriteriaQuery<UserDbo> cq = createQuery();
        Root<UserDbo> root = cq.from(UserDbo.class);
        List<Predicate> preds = new ArrayList<>();
        if (!isNullOrEmpty(userName))
            preds.add(cb().equal(root.get(UserDbo_.userName), userName.trim()));
        if (!isNullOrEmpty(email))
            preds.add(cb().equal(root.get(UserDbo_.email), email.trim()));
        if (!isNullOrEmpty(encryptedPassword))
            preds.add(cb().equal(root.get(UserDbo_.encryptedPassword), encryptedPassword));
        return getSingleResult(cq, preds);
    }

    @Override
    public UserDbo refresh(UserDbo user) {
        return super.refresh(user);
    }
}
