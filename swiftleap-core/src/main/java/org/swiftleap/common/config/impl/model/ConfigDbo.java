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
package org.swiftleap.common.config.impl.model;

import lombok.*;
import org.swiftleap.common.persistance.GenericEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ruans on 2017/06/08.
 */
@Entity
@Table(name = "cnf_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDbo implements GenericEntity<ConfigDbo.ConfigId> {
    @EmbeddedId
    ConfigId id;
    @Column(name = "value")
    String value;

    public ConfigDbo(String section, String id, String value) {
        this.id = new ConfigId(section, id);
        this.value = value;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfigId implements Serializable {
        @Column(name = "section")
        String section;
        @Column(name = "config_key")
        String key;
    }
}
