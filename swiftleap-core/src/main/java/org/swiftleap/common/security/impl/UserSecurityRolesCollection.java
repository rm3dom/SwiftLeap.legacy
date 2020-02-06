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


import org.swiftleap.common.security.*;

import java.util.Collection;

/**
 * @author ruan
 */
public class UserSecurityRolesCollection<E extends SecRole> extends AbstractSecurityRolesCollection<E> {

    User principal;

    public UserSecurityRolesCollection(Collection<E> backingColl, User principal) {
        super(backingColl, principal);
        this.principal = principal;
    }

    @Override
    public E addGet(SecRole e) {
        if (!(e instanceof UserSecRole)) {
            SecurityService sc = SecurityContext.getSecurityService();
            //Create and add a managed instance.
            return (E) sc.createSecurityRole(principal, e);
        }
        return super.addGet((E) e);
    }

}
