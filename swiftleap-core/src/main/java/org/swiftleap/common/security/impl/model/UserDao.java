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

import org.swiftleap.common.persistance.GenericDao;

import java.util.stream.Stream;

public interface UserDao extends GenericDao<UserDbo, Long> {
    Stream<UserDbo> findUsersByParty(Long partyId);

    UserDbo findUserByCred(String userName, String email, String encryptedPassword);

    UserDbo refresh(UserDbo user);
}
