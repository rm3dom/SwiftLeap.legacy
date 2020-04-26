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
package org.swiftleap.common.security.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.swiftleap.common.codegen.anotate.CGAlias;
import org.swiftleap.common.codegen.anotate.CGInclude;
import org.swiftleap.common.security.*;
import org.swiftleap.common.types.ImageData;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dean on 17/09/16.
 */
@CGAlias("User")
@Builder
@Getter
@Setter
@CGInclude
public class UserDto implements Tenanted, Principal {
    Long id;
    Integer tenantId;
    String userName;
    String email;
    boolean activated = true;
    List<String> roles;
    String firstName;
    String surname;
    String sessionId;
    byte[] image;
    String imageMime;
    /**
     * True if managed by this system. False if the user was authenticated externally.
     */
    boolean managed = false;

    @Tolerate
    public UserDto() {
    }

    @Tolerate
    public UserDto(UserPrincipal principal, String sessionId) {
        this.tenantId = principal.getTenantId();
        this.userName = principal.getUserName();
        this.sessionId = sessionId;
        roles = principal.getPrincipalRoles()
                .stream()
                .map(SecRoleCode::getCode)
                .collect(Collectors.toList());

        if(principal instanceof User) {
            User user = (User) principal;
            this.managed = true;
            this.id = user.getId();
            this.email = user.getEmail();
            this.activated = user.getStatus().isActive();
            this.firstName = user.getFirstName();
            this.surname = user.getSurname();

            ImageData image = user.getImage();
            if (image != null) {
                this.imageMime = image.getMime();
                this.image = image.getData(ImageData.ImageSize.SMALL);
            }
        }
    }

    @Override
    public String getName() {
        return userName;
    }
}
