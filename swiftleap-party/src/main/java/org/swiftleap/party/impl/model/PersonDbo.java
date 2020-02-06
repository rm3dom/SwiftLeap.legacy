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
import lombok.Getter;
import lombok.Setter;
import org.swiftleap.common.util.PhoneUtil;
import org.swiftleap.party.PartyEnums;
import org.swiftleap.party.Person;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by ruans on 2017/05/02.
 */
@Entity
@Table(name = "pty_person", uniqueConstraints = {
        @UniqueConstraint(name = "pty_person_email", columnNames = {"ref_tenant_id", "email"}),
        @UniqueConstraint(name = "pty_person_email_alt", columnNames = {"ref_tenant_id", "email_alt"})
})
@DiscriminatorValue("prs")
@Getter
@Setter
public class PersonDbo extends PartyDbo implements Person {
    @Column(name = "ref_tenant_id")
    Integer refTenantId;
    @Column(name = "first_name", nullable = false, length = 64)
    String firstName;
    @Column(name = "surname", nullable = false, length = 64)
    String surname;
    @Column(name = "dob")
    Date dateOfBirth;
    @Column(name = "gender")
    PartyEnums.Gender gender = PartyEnums.Gender.UNKNOWN;
    @Column(name = "email", length = 128)
    String email;
    @Column(name = "email_alt", length = 128)
    String emailAlt;
    @Column(name = "mobile_no", length = 24)
    String mobileNo;
    @Column(name = "mobile_no_alt", length = 24)
    String mobileNoAlt;


    public PersonDbo() {
        type = "prs";
    }

    public Integer getTenantId() {
        refTenantId = super.getTenantId();
        return refTenantId;
    }

    @Override
    public void setTenantId(Integer tenantId) {
        super.setTenantId(tenantId);
        this.refTenantId = tenantId;
    }

    public void setMobileNo(String number, String defaultCountryCode) throws NumberParseException {
        if (defaultCountryCode == null)
            defaultCountryCode = countryCode;
        mobileNo = PhoneUtil.formatDb(number, defaultCountryCode);
    }

    public String getMobileNo() {
        return PhoneUtil.reverse(mobileNo);
    }

    public void setMobileNo(String number) throws NumberParseException {
        setMobileNo(number, number);
    }

    public void setMobileNoAlt(String number, String defaultCountryCode) throws NumberParseException {
        if (defaultCountryCode == null)
            defaultCountryCode = countryCode;
        mobileNoAlt = PhoneUtil.formatDb(number, defaultCountryCode);
    }

    public String getMobileNoAlt() {
        return PhoneUtil.reverse(mobileNoAlt);
    }

    public void setMobileNoAlt(String number) throws NumberParseException {
        setMobileNoAlt(number, null);
    }

    @Override
    public String getType() {
        return "prs";
    }

    @Override
    public void setType(String type) {
        type = "prs";
    }

    @Override
    public String getDescription() {
        return firstName + " " + surname;
    }
}
