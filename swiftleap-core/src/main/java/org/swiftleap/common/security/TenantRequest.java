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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.swiftleap.common.codegen.anotate.CGDefault;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruans on 2017/06/08.
 */
@Builder
@Getter
@Setter
public class TenantRequest {
    Integer id;
    String name;
    String longName;
    String countryCode;
    String fqdn;
    long partyId;
    String email;
    String userName;
    String password;
    String firstName;
    String surname;
    @CGDefault(elm = "True")
    boolean activated = true;
    @JsonIgnore
    Map<String, String> config = new HashMap<>();

    @Tolerate
    public TenantRequest() {
    }
}
