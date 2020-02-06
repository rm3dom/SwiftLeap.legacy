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
import org.swiftleap.common.persistance.TenantedEntity;
import org.swiftleap.common.security.SecRole;
import org.swiftleap.common.security.SecurityRolesCollection;
import org.swiftleap.common.security.User;
import org.swiftleap.common.security.UserSecRole;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "sec_role_user")
@Getter
@Setter
public class UserSecRoleDbo implements UserSecRole, TenantedEntity<Long> {
    @Column(name = "tenant_id", nullable = false)
    Integer tenantId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    Long userId;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserDbo.class, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_sec_role_user_user"))
    User user;
    @Column(name = "role_id", insertable = false, updatable = false, nullable = false)
    Long roleId;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = SecRoleDefineDbo.class, optional = false)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_sec_role_user_role"))
    SecRole role;

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(this.role, obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.role);
    }


    @Override
    public String getCode() {
        return role != null ? role.getCode() : null;
    }

    public void setUser(User user) {
        this.userId = user == null ? null : user.getUserId();
        this.user = user;
    }

    public void setRole(SecRole role) {
        if (role != null) {
            roleId = role.getRoleId();
        } else {
            roleId = null;
        }
        this.role = (SecRoleDefineDbo) role;
    }

    @Override
    public String getName() {
        return role != null ? role.getName() : null;
    }

    @Override
    public String getDescription() {
        return role != null ? role.getDescription() : null;
    }

    @Override
    public SecRole.Status getStatus() {
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
    public void update(String code, String name, String description, SecRole.Status status) {
        if (role == null) {
            throw new NullPointerException("Null role");
        }
        this.role.update(code, name, description, status);
    }
}
