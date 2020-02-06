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


import org.swiftleap.common.persistance.GenericEntity;
import org.swiftleap.common.types.EnumValueType;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Roles define the permissions of an entity, NOT THE TYPE OF ENTITY. Do not assume that an Employer will have an 'Employer' role.
 */
public interface SecRole extends SecRoleIdentifier, GenericEntity<Long> {

    String getName();

    String getDescription();

    Status getStatus();

    boolean isActive();

    SecurityRolesCollection<SecRole> getDelegates();

    void update(String code, String name, String description, Status status);

    default Stream<SecRoleIdentifier> getDelegatesRecursive(Set<SecRoleIdentifier> visited) {
        return getDelegates().stream()
                .filter(SecRole::isActive)
                .flatMap(r -> visited.add(r) ? Stream.concat(Stream.of(r), r.getDelegatesRecursive(visited)) : Stream.empty());
    }

    enum Status implements EnumValueType<Character> {

        ACTIVE('A'),
        INACTIVE('N');

        Character value;

        private Status(char value) {
            this.value = value;
        }

        @Override
        public Character getValue() {
            return value;
        }
    }
}
