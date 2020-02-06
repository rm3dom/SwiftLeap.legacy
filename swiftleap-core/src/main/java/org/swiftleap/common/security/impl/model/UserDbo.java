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
import lombok.val;
import org.swiftleap.cms.image.ImageTemplate;
import org.swiftleap.common.collection.ReadOnlyCollection;
import org.swiftleap.common.collection.ReadOnlyCollectionImpl;
import org.swiftleap.common.persistance.AuditInfo;
import org.swiftleap.common.persistance.Audited;
import org.swiftleap.common.persistance.TenantedEntity;
import org.swiftleap.common.security.*;
import org.swiftleap.common.security.impl.SecurityAppCtx;
import org.swiftleap.common.security.impl.UserSecurityRolesCollection;
import org.swiftleap.common.types.ImageData;

import javax.persistence.*;
import javax.security.auth.Subject;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ruan
 */
@Entity
@Table(name = "sec_user", uniqueConstraints = {
        @UniqueConstraint(name = "UK_sec_user_user_name", columnNames = {"tenant_id", "user_name"}),
        @UniqueConstraint(name = "UK_sec_user_email", columnNames = {"tenant_id", "email"})})
@Getter
@Setter
public class UserDbo implements User, TenantedEntity<Long>, Audited<AuditInfo> {
    @Column(name = "tenant_id", nullable = false)
    Integer tenantId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "party_id")
    Long partyId;
    @Column(name = "bin", nullable = false)
    Integer bin = 1;
    @Column(name = "user_name", length = 32, nullable = true)
    String userName;
    @Column(name = "description", length = 128)
    String description;
    @Column(name = "email", length = 128, nullable = true)
    String email;
    @Column(name = "enc_password", length = 128, nullable = true)
    String encryptedPassword;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "password_changed")
    Date passwordChangedDate = new Date();
    @Column(name = "bio_req")
    boolean bioAuthRequired = false;
    @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, targetEntity = UserSecRoleDbo.class, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<SecRole> securityRoles;
    @Column(name = "status", nullable = false)
    UserStatus status = UserStatus.ACTIVE;
    @Embedded
    AuditInfo auditInfo = new AuditInfo();
    @Column(name = "first_name")
    String firstName;
    @Column(name = "surname")
    String surname;


    @Override
    public Long getUserId() {
        return getId();
    }

    @Override
    public String getName() {
        return getUserName();
    }

    @Override
    public void setCredentials(String newUserName, String newEmail, boolean bioAuthReq) {
        if (newUserName != null) {
            userName = newUserName;
            if (newUserName.isEmpty()) {
                userName = null;
            }
        }
        if (newEmail != null) {
            email = newEmail;
            if (newEmail.isEmpty()) {
                email = null;
            }
        }
        this.bioAuthRequired = bioAuthReq;
    }

    @Override
    public void setPassword(String newPassword) {
        EncryptionController encc = SecurityAppCtx.getEncryptionController();
        String ep;
        try {
            ep = encc.encryptPassword(newPassword);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        encryptedPassword = ep;
    }

    @Override
    public void setCredentials(String newUserName, String newEmail, String newPassword, boolean bioAuthReq) {

        EncryptionController encc = SecurityAppCtx.getEncryptionController();

        String ep = null;
        if (newPassword != null) {
            try {
                //TODO Older systems do indicate if the password is obfuscated.
                //String clearPassword = encc.unObfuscatePassword(newPassword);
                ep = encc.encryptPassword(newPassword);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        if (ep != null) {
            encryptedPassword = ep;
        }
        if (newUserName != null) {
            userName = newUserName;
            if (newUserName.isEmpty()) {
                userName = null;
            }
        }
        if (newEmail != null) {
            email = newEmail;
            if (newEmail.isEmpty()) {
                email = null;
            }
        }
        bioAuthRequired = bioAuthReq;
    }

    @Override
    public boolean passwordMatches(String password) {
        try {
            EncryptionController secc = SecurityAppCtx.getEncryptionController();
            return Objects.equals(encryptedPassword, secc.encryptPassword(password));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return getDescription();
    }

    /**
     * @return the encryptedPassword
     */
    @Override
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * @param encryptedPassword the encryptedPassword to set
     */
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    @Override
    public SecurityRolesCollection<SecRole> getSecurityRoles() {
        return new UserSecurityRolesCollection<>(securityRoles, this);
    }

    @Override
    public ReadOnlyCollection<SecRole> getAllowedDelegation() {
        return new ReadOnlyCollectionImpl<>(SecurityUtil.getAllowedDelegation(this, false));
    }

    @Override
    public ReadOnlyCollection<SecRoleIdentifier> getPrincipalRoles() {
        val visited = new HashSet<SecRoleIdentifier>();
        val list = securityRoles.stream()
                .filter(SecRole::isActive)
                .flatMap(r -> visited.add(r) ? Stream.concat(Stream.of(r), r.getDelegatesRecursive(visited)) : Stream.empty())
                .collect(Collectors.toList());
        return new ReadOnlyCollectionImpl<>(list);
    }


    public boolean implies(Subject subject) {
        if (subject == null)
            return false;
        return subject.getPrincipals().contains(this);
    }

    @Override
    public boolean isGuest() {
        return (userName != null && userName.equalsIgnoreCase("guest")) || (getUserId() != null && getUserId() < 0);
    }

    @Override
    public boolean isSuper() {
        return (userName != null && userName.equalsIgnoreCase("system"))
                || (getPrincipalRoles().stream().anyMatch(r -> r.getCode().equalsIgnoreCase("system") || r.getCode().equalsIgnoreCase("sysadm")));
    }

    private String getPath() {
        if (id == null)
            return "";
        return "users/" + (id / 1000) + "/" + (id % 1000);
    }

    private ImageTemplate getImageTemplate() {
        return new ImageTemplate(SecurityAppCtx.getCmsService(), getPath());
    }

    @Override
    public ImageData getImage() {
        return getImageTemplate().getImage("user");
    }

    @Override
    public void deleteImage() throws IOException {
        getImageTemplate().deleteImage("user");
    }

    @Override
    public ImageData setImage(String mime, String fileName, byte[] data) throws IOException {
        return getImageTemplate().setImage("user", "User Image", mime, fileName, data);
    }
}
