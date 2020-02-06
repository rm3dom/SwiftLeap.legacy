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

package org.swiftleap.common.security;


import org.swiftleap.common.collection.ReadOnlyCollection;
import org.swiftleap.common.persistance.TenantedEntity;
import org.swiftleap.common.types.EnumValueType;
import org.swiftleap.common.types.ImageData;

import java.io.IOException;
import java.util.Date;

/**
 * @author ruan
 */
public interface User extends UserPrincipal, TenantedEntity<Long> {

    void setPassword(String password);

    String getFirstName();

    void setFirstName(String val);

    String getSurname();

    void setSurname(String val);

    Long getPartyId();

    String getEmail();

    void setEmail(String email);

    String getEncryptedPassword();

    boolean passwordMatches(String password);

    Date getPasswordChangedDate();

    boolean isBioAuthRequired();

    /**
     * Only non null values are set; empty strings are nulls.
     */
    void setCredentials(String name, String email, boolean bioAuthReq);

    /**
     * Only non null values are set; empty strings are nulls.
     */
    void setCredentials(String name, String email, String password, boolean bioAuthReq);


    void setDescription(String description);

    /**
     * All roles assigned to the user including inactive ones.
     *
     * @return roles.
     */
    SecurityRolesCollection<SecRole> getSecurityRoles();

    /**
     * All the roles the user is allowed to delegate including inactive ones.
     *
     * @return roles.
     */
    ReadOnlyCollection<SecRole> getAllowedDelegation();


    UserStatus getStatus();

    void setStatus(UserStatus status);

    ImageData getImage();

    void setUserName(String userName);

    void deleteImage() throws IOException;

    ImageData setImage(String mime, String fileName, byte[] data) throws IOException;

    enum UserStatus implements EnumValueType<Character> {
        ACTIVE('A'),
        INACTIVE('I'),
        UNKNOWN('_');

        char value;

        private UserStatus(char value) {
            this.value = value;
        }

        @Override
        public Character getValue() {
            return value;
        }

        public boolean isActive() {
            return this == ACTIVE;
        }
    }
}
