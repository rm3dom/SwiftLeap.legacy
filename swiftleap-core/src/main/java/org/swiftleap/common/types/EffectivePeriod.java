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


import org.swiftleap.common.util.DateUtil;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Objects effective for a certain period.
 * <p/>
 * Additional utilities such as special collections may make use of this.
 * <p/>
 * * Created by ruan on 2015/08/08.
 */
public interface EffectivePeriod extends Effective {

    /**
     * Order by endDate descending.
     */
    Comparator<EffectivePeriod> END_DESC = (o1, o2) -> {
        if (o1.equals(o2)) return 0;
        int ret = DateUtil.dateCompare(o1.getEndDate(), o2.getEndDate());
        if (ret != 0) return ret * -1;
        ret = DateUtil.dateCompare(o1.getStartDate(), o2.getStartDate());
        return (ret == 0 ? -1 : ret) * -1;
    };
    /**
     * Order by endDate ascending.
     */
    Comparator<EffectivePeriod> END_ASC = (o1, o2) -> {
        if (o1.equals(o2)) return 0;
        int ret = DateUtil.dateCompare(o1.getEndDate(), o2.getEndDate());
        if (ret != 0) return ret;
        ret = DateUtil.dateCompare(o1.getStartDate(), o2.getStartDate());
        return ret == 0 ? -1 : ret;
    };
    /**
     * Order by startDate ascending.
     */
    Comparator<EffectivePeriod> START_ASC = (o1, o2) -> {
        if (o1.equals(o2)) return 0;
        int ret = DateUtil.dateCompare(o1.getStartDate(), o2.getStartDate());
        if (ret != 0) return ret;
        ret = DateUtil.dateCompare(o1.getEndDate(), o2.getEndDate());
        return ret == 0 ? 1 : ret;
    };
    /**
     * Order by startDate descending.
     */
    Comparator<EffectivePeriod> START_DESC = (o1, o2) -> {
        if (o1.equals(o2)) return 0;
        int ret = DateUtil.dateCompare(o1.getStartDate(), o2.getStartDate());
        if (ret != 0) return ret * -1;
        ret = DateUtil.dateCompare(o1.getEndDate(), o2.getEndDate());
        return ret == 0 ? 1 : ret * -1;
    };

    Date getEndDate();

    default int getStartDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartDate());
        return cal.get(Calendar.DAY_OF_WEEK);
    }


    default int getStartHour() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartDate());
        return cal.get(Calendar.HOUR_OF_DAY);
    }
}
