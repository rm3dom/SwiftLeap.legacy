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
package org.swiftleap.common.persistance.impl;

import lombok.Getter;
import lombok.Setter;
import org.swiftleap.common.persistance.GenericEntity;

import javax.persistence.*;

/**
 * Created by ruans on 2017/06/05.
 */
@Entity
@Table(name = "seq_seq", uniqueConstraints = {
        @UniqueConstraint(name = "UK_seq_seq", columnNames = {"seq_name"})})
@Getter
@Setter
public class SequenceDbo implements GenericEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "seq_name")
    String name;
    @Column(name = "seq")
    int nextSequence;
}
