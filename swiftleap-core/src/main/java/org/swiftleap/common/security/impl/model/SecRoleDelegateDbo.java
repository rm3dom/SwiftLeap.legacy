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

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sec_role_del", uniqueConstraints =
@UniqueConstraint(name = "UK_sec_role_del_role_id", columnNames = {"del_role_id", "role_id"}))
@Getter
@Setter
public class SecRoleDelegateDbo implements SecRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "del_role_id", insertable = false, updatable = false, nullable = false)
    Long delegatingRoleId;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = SecRoleDefineDbo.class, optional = false)
    @JoinColumn(name = "del_role_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_sec_role_del_del"))
    SecRole delegatingRole;
    @Column(name = "role_id", insertable = false, updatable = false, nullable = false)
    Long roleId;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = SecRoleDefineDbo.class, optional = false)
    @JoinColumn(name = "role_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_sec_role_del_role"))
    SecRole role;

    public SecRoleDelegateDbo() {
    }

    public SecRoleDelegateDbo(SecRole delegatingRole, SecRole role) {
        this.delegatingRoleId = delegatingRole != null ? delegatingRole.getRoleId() : null;
        this.delegatingRole = delegatingRole;
        this.roleId = role != null ? role.getRoleId() : null;
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(this.role, obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.role);
    }

    public void setRole(SecRole role) {
        this.roleId = role != null ? role.getRoleId() : null;
        this.role = role;
    }

    public void setDelegateRole(SecRole delegateRole) {
        this.delegatingRoleId = delegateRole != null ? delegateRole.getRoleId() : null;
        this.delegatingRole = delegateRole;
    }

    @Override
    public String getCode() {
        return role != null ? role.getCode() : null;
    }

    @Override
    public Long getRoleId() {
        return role != null ? role.getRoleId() : null;
    }

    @Override
    public String getDescription() {
        return role != null ? role.getDescription() : null;
    }

    @Override
    public String getName() {
        return role != null ? role.getName() : null;
    }

    @Override
    public Status getStatus() {
        return role != null ? role.getStatus() : null;
    }

    @Override
    public SecurityRolesCollection<SecRole> getDelegates() {
        return role != null ? role.getDelegates() : null;
    }

    @Override
    public boolean isActive() {
        if (role == null) {
            throw new NullPointerException("Null role");
        }
        return role.isActive();
    }

    @Override
    public void update(String code, String name, String description, Status status) {
        this.role.update(code, name, description, status);
    }
}
