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


import lombok.Getter;
import lombok.Setter;
import org.swiftleap.common.security.SecRole;
import org.swiftleap.common.security.SecurityRolesCollection;
import org.swiftleap.common.security.impl.DelegateRolesCollection;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sec_role_def", uniqueConstraints = {
        @UniqueConstraint(name = "UK_sec_role_def_code", columnNames = "code")})
@Getter
@Setter
public class SecRoleDefineDbo implements SecRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "code", nullable = false)
    String code;
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "description", nullable = false)
    String description;
    @Column(name = "status", nullable = false)
    SecRole.Status status = SecRole.Status.INACTIVE;
    @OneToMany(mappedBy = "delegatingRoleId", fetch = FetchType.EAGER, targetEntity = SecRoleDelegateDbo.class, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<SecRole> delegates;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SecRole)) {
            return false;
        }

        final SecRole other = (SecRole) obj;
        if ((this.code == null) ? (other.getCode() != null) : !this.code.equals(other.getCode())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.code);
        return hash;
    }


    public void setCode(String code) {
        if (code != null) {
            code = code.toUpperCase();
        }
        this.code = code;
    }


    @Override
    public Long getRoleId() {
        return getId();
    }

    @Override
    public SecurityRolesCollection<SecRole> getDelegates() {
        return new DelegateRolesCollection<SecRole>(delegates, this);
    }

    @Override
    public boolean isActive() {
        return Status.ACTIVE.equals(status);
    }

    @Override
    public void update(String code, String name, String description, Status status) {
        if (code != null) {
            this.code = code;
        }
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (status != null) {
            this.status = status;
        }
    }
}
