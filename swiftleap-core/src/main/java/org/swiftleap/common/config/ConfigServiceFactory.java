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
package org.swiftleap.common.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swiftleap.common.config.impl.ConfigServiceImpl;
import org.swiftleap.common.config.impl.model.ConfigDao;
import org.swiftleap.common.util.Singleton;

/**
 * Created by ruans on 2017/06/12.
 */
@Component
public class ConfigServiceFactory implements FactoryBean<ConfigService> {

    private static Singleton<ConfigServiceImpl> instance = new Singleton<>(ConfigServiceImpl::new);

    public static ConfigService getInstance() {
        return instance.get();
    }

    @Autowired
    public void setConfigDao(ConfigDao configDao) {
        instance.get().setConfigDao(configDao);
    }

    @Override
    public ConfigService getObject() throws Exception {
        return instance.get();
    }

    @Override
    public Class<?> getObjectType() {
        return ConfigService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
