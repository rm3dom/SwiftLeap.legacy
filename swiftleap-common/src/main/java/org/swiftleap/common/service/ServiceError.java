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


package org.swiftleap.common.service;


import javax.servlet.ServletException;
import java.util.Random;

/**
 * A common interface for checked and unchecked exceptions.
 * All exceptions derived from this will be localized.
 * <p/>
 * Created by ruan on 2015/08/08.
 */
public interface ServiceError {

    static Throwable unwind(Throwable ex) {
        if (ex instanceof ServletException && ex.getCause() != null) {
            return unwind(ex.getCause());
        }
        return ex;
    }

    static String getReferenceNo() {
        Random random = new Random();
        //65 - 90
        String ret = "#";
        for (int i = 0; i < 5; i++) {
            ret += (char) (Math.abs(random.nextInt() % 25) + 65);

        }
        return ret;
    }

    /**
     * A random reference no.
     * This reference should appear in the logs and be shown to the user.
     * Normally time and description is not enough to find the cause of the error.
     *
     * @return A random reference number.
     */
    String getReference();
}

