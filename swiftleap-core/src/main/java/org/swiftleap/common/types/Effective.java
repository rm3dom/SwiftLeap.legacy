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
package org.swiftleap.common.types;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Objects effective from a certain date.
 * <p/>
 * Additional utilities such as special collections may make use of this.
 * <p/>
 * <p/>
 * * Created by ruan on 2015/08/08.
 */
public interface Effective {
    Date END_OF_TIME = new GregorianCalendar(9999, 11, 31).getTime();
    Date END_OF_DATE = new GregorianCalendar(9999, 11, 31).getTime();
    Date START_OF_TIME = new GregorianCalendar(1970, 0, 1).getTime();
    Date START_OF_DATE = new GregorianCalendar(1700, 0, 1).getTime();

    Date getStartDate();
}
