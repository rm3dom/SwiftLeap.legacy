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
package org.swiftleap.party.impl;

import com.google.i18n.phonenumbers.NumberParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swiftleap.common.config.ConfigService;
import org.swiftleap.common.security.SecurityContext;
import org.swiftleap.common.security.Tenant;
import org.swiftleap.common.service.BadRequestException;
import org.swiftleap.party.*;
import org.swiftleap.party.impl.model.OrganizationDao;
import org.swiftleap.party.impl.model.PartyDao;
import org.swiftleap.party.impl.model.PersonDao;
import org.swiftleap.party.impl.model.PersonDbo;

import java.util.stream.Stream;

/**
 * Created by ruans on 2017/05/02.
 */
@Service
public class PartyServiceImpl implements PartyService {

    @Autowired
    PersonDao personDao;
    @Autowired
    OrganizationDao organizationDao;
    @Autowired
    PartyDao partyDao;
    @Autowired
    ConfigService configService;

    @Override
    public Person createPerson(PersonRequest request) {
        Tenant tenant = SecurityContext.getTenant();

        String countryCode = request.getCountryCode();
        if (countryCode == null)
            countryCode = tenant.getCountryCode();

        PersonDbo person = new PersonDbo();
        person.setDateOfBirth(request.getDateOfBirth());
        person.setEmail(request.getEmail());
        person.setFirstName(request.getFirstName());
        person.setSurname(request.getSurname());
        person.setGender(request.getGender());
        person.setCountryCode(countryCode);
        person.setRegChannel(request.getChannel());

        try {
            person.setMobileNo(request.getMobileNo(), countryCode);
        } catch (NumberParseException e) {
            throw new BadRequestException("Invalid mobile no");
        }

        return personDao.persist(person);
    }

    @Override
    public Stream<? extends Person> findPerson(String firstName, String surname, PartyEnums.Gender gender, String email, String mobile) {
        return personDao.find(firstName, surname, gender, email, mobile);
    }

    @Override
    public Party getPartyById(long id) {
        return partyDao.findById(id);
    }

    @Override
    public Person findPerson(String email, String mobileNo) {
        return personDao.find(null, null, null, email, mobileNo)
                .findFirst()
                .orElse(null);
    }
}
