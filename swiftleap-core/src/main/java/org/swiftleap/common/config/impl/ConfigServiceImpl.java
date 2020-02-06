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
package org.swiftleap.common.config.impl;

import org.swiftleap.common.config.Config;
import org.swiftleap.common.config.ConfigService;
import org.swiftleap.common.config.impl.model.ConfigDao;
import org.swiftleap.common.config.impl.model.ConfigDbo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruans on 2017/06/08.
 */
public class ConfigServiceImpl implements ConfigService {

    ConfigDao configDao;

    public void setConfigDao(ConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public Config getConfig(final String section) {
        final Map<String, String> map = new HashMap<>();
        configDao.findAll().forEach(c -> map.put(c.getId().getKey(), c.getValue()));
        return new ConfigMap(map) {
            @Override
            public void delete(String key) {
                map.remove(key);
                configDao.deleteById(new ConfigDbo.ConfigId(section, key));
            }

            @Override
            public void set(String key, Object value) {
                if (value == null)
                    delete(key);
                else {
                    map.put(key, value.toString());
                    configDao.persist(new ConfigDbo(new ConfigDbo.ConfigId(section, key), value.toString()));
                }
            }
        };
    }

    public abstract class ConfigMap implements Config {
        Map<String, String> map = new HashMap<>();

        public ConfigMap(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public String get(String key) {
            return map.get(key);
        }

        @Override
        public String get(String key, String def) {
            String v = map.get(key);
            if (v == null || v.isEmpty())
                return def;
            return v;
        }

        @Override
        public long get(String key, long def) {
            String v = map.get(key);
            if (v == null || v.isEmpty())
                return def;
            return Long.parseLong(v);
        }
    }
}
