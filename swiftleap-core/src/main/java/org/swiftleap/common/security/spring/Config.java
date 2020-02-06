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
package org.swiftleap.common.security.spring;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.swiftleap.common.config.PropKeys;
import org.swiftleap.common.security.impl.model.GlobalUserDao;
import org.swiftleap.common.security.impl.model.TenantedUserDao;
import org.swiftleap.common.security.impl.model.UserDao;

@Configuration("org.swiftleap.common.security.spring")
public class Config {
    @Value(value = PropKeys._SECURITY_GLOBAL_USERS)
    boolean globalUsers;

    @Bean
    public UserDao userDao() {
        if (globalUsers)
            return new GlobalUserDao();
        return new TenantedUserDao();
    }
}
