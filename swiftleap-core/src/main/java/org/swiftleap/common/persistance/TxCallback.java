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

/**
 * Execute logic in a transaction.
 * <p>
 * This works in a similar fashion to Springs TransactionTemplate
 * with the exception that it does not rollback on a checked exception.
 * </p>
 *
 * @see TxTemplate
 * Created by ruan on 2015/08/10.
 */
@FunctionalInterface
public interface TxCallback<T> {
    T doInTransaction() throws Exception;
}
