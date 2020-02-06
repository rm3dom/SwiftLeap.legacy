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

import com.google.i18n.phonenumbers.NumberParseException;
import org.springframework.stereotype.Repository;
import org.swiftleap.common.persistance.TenantedJpaDao;
import org.swiftleap.common.service.BadRequestException;
import org.swiftleap.common.util.PhoneUtil;
import org.swiftleap.party.PartyEnums;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/06/11.
 */
@Repository
public class PersonDao extends TenantedJpaDao<PersonDbo, Long> {
    @PersistenceContext(unitName = "corePersistenceUnit")
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Stream<PersonDbo> find(String firstName, String surname, PartyEnums.Gender gender, String email, String mobileNo) {
        CriteriaQuery<PersonDbo> cq = createQuery();
        Root<PersonDbo> root = cq.from(PersonDbo.class);
        List<Predicate> preds = new ArrayList<>();
        if (!isNullOrEmpty(firstName))
            preds.add(cb().equal(root.get(PersonDbo_.firstName), firstName));
        if (!isNullOrEmpty(surname))
            preds.add(cb().equal(root.get(PersonDbo_.surname), surname));
        if (!isNullOrEmpty(gender))
            preds.add(cb().equal(root.get(PersonDbo_.gender), gender));
        if (!isNullOrEmpty(email)) {
            preds.add(cb().or(
                    cb().equal(root.get(PersonDbo_.email), email),
                    cb().equal(root.get(PersonDbo_.emailAlt), email)));
        }
        if (!isNullOrEmpty(mobileNo)) {
            try {
                String no = PhoneUtil.formatDb(mobileNo);
                preds.add(cb().or(
                        cb().equal(root.get(PersonDbo_.mobileNo), no),
                        cb().equal(root.get(PersonDbo_.mobileNoAlt), no)));
            } catch (NumberParseException e) {
                throw new BadRequestException("Invalid mobile no");
            }
        }

        return getResultList(cq, preds);
    }
}