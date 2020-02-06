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
package org.swiftleap.party.impl.model;

import lombok.Getter;
import lombok.Setter;
import org.swiftleap.party.Organization;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by ruans on 2017/05/02.
 */
@Entity
@Table(name = "pty_org")
@DiscriminatorValue("org")
@Getter
@Setter
public class OrganizationDbo extends PartyDbo implements Organization {
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "long_name")
    String longName;

    public OrganizationDbo() {
        type = "org";
    }

    @Override
    public String getType() {
        return "org";
    }

    @Override
    public void setType(String type) {
        type = "org";
    }

    @Override
    public String getDescription() {
        return name;
    }
}
