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
package org.swiftleap.party;

import java.util.Date;

/**
 * Created by ruans on 2017/06/04.
 */
public interface Person extends Party {
    String getDescription();

    String getFirstName();

    String getSurname();

    Date getDateOfBirth();

    PartyEnums.Gender getGender();

    String getEmail();

    String getEmailAlt();

    String getMobileNo();

    String getMobileNoAlt();
}
