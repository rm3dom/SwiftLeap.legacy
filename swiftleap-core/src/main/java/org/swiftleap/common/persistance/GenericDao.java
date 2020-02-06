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
package org.swiftleap.common.persistance;

import org.swiftleap.common.types.Range;
import org.swiftleap.common.util.BeanUtil;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * The basic generic DAO.
 *
 * @param <T>  The type of object to be managed by this DAO.
 * @param <ID> The primary/composite key of the managed object.
 */
@SuppressWarnings({"UnusedDeclaration"})
public interface GenericDao<T extends GenericEntity<ID>, ID extends Serializable>
        extends org.springframework.data.repository.Repository<T, ID> {

    boolean exists(ID id);

    T findById(ID id);

    T findById(ID id, boolean lock);

    Stream<T> findAll();

    <E extends T> E persist(E entity);

    void delete(Object entity);

    void flush();

    void clear();

    Stream<T> findRange(Range range);

    int count();


    default <E extends T> E copyOf(E t) {
        return BeanUtil.copy(t);
    }
}
