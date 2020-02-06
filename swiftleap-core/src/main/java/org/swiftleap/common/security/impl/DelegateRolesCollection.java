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

package org.swiftleap.common.security.impl;


import org.swiftleap.common.security.SecRole;
import org.swiftleap.common.security.SecurityContext;
import org.swiftleap.common.security.SecurityService;
import org.swiftleap.common.security.impl.model.SecRoleDelegateDbo;

import java.util.Collection;

/**
 * @author ruan
 */
public class DelegateRolesCollection<E extends SecRole> extends AbstractSecurityRolesCollection<E> {

    SecRole owner;

    public DelegateRolesCollection(Collection<E> backingColl, SecRole owner) {
        super(backingColl, owner);
        this.owner = owner;
    }

    @Override
    public E addGet(SecRole e) {
        if (!(e instanceof SecRoleDelegateDbo)) {
            SecurityService sc = SecurityContext.getSecurityService();
            return (E) sc.createSecurityDelegate(owner, e);
        }
        return super.addGet((E) e);
    }
}
