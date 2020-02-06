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

import javax.transaction.*;

/**
 * Execute logic in a transaction.
 * <p>
 * This works in a similar fashion to Springs TransactionTemplate
 * with the exception that it does not rollback on a checked exception.
 * </p>
 * Created by ruan on 2015/08/10.
 */
public class TxTemplate {
    protected UserTransaction userTransaction;

    public TxTemplate(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    protected TxTemplate() {
    }

    public <T> T execute(TxCallback<T> action) throws Exception {
        return executeInUserTransaction(action);
    }

    protected <T> T executeInUserTransaction(TxCallback<T> action) throws Exception {
        userTransaction.begin();
        try {
            T ret = action.doInTransaction();
            userTransaction.commit();
            return ret;
        } catch (RollbackException
                | HeuristicMixedException
                | HeuristicRollbackException
                | SecurityException
                | IllegalStateException
                | SystemException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            userTransaction.rollback();
            throw ex;
        } catch (Exception ex) {
            userTransaction.commit();
            throw ex;
        }
    }
}
