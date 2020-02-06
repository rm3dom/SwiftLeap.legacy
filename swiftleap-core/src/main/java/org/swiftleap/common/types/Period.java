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

import java.util.Date;

/**
 * Created by ruan on 2015/08/10.
 */
public class Period implements EffectivePeriod {
    Date startDate;
    Date endDate;

    public Period() {
    }

    public Period(EffectivePeriod effectivePeriod) {
        this.startDate = effectivePeriod.getStartDate();
        this.endDate = effectivePeriod.getEndDate();
    }

    public Period(Date startDate, Date endDate) {
        this.startDate = DateUtil.makeStartOfDay(startDate);
        this.endDate = DateUtil.makeEndOfDay(endDate);
    }

    public static Period makeNoThrow(String from, String to) {
        Period ret = new Period();
        try {
            ret.setStartDate(DateUtil.make(from));
            ret.setEndDate(DateUtil.make((to)));
        } catch (Exception ex) {
            return new Period();
        }
        return ret;
    }

    public boolean isValid() {
        return startDate != null && endDate != null && startDate.compareTo(endDate) < 1;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = DateUtil.makeStartOfDay(startDate);
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = DateUtil.makeEndOfDay(endDate);
    }

    public boolean isContinuous(EffectivePeriod other) {
        return DateUtil.isContinuous(this, other);
    }

    public void grow(EffectivePeriod period) {
        setStartDate(DateUtil.min(getStartDate(), period.getStartDate()));
        setEndDate(DateUtil.max(getEndDate(), period.getEndDate()));
    }

    public boolean contains(EffectivePeriod right) {
        return DateUtil.contains(this, right);
    }

    public boolean contains(Date right) {
        return DateUtil.contains(this, right);
    }

    public boolean overlaps(EffectivePeriod right) {
        return DateUtil.overlaps(this, right);
    }


    public int daysBetween() {
        return DateUtil.getDaysBetween(getStartDate(), getEndDate());
    }

    public void intersects(EffectivePeriod right) {
        Period intersection = DateUtil.intersection(this, right);
        startDate = intersection.getStartDate();
        endDate = intersection.getEndDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Period period = (Period) o;

        if (startDate != null ? !startDate.equals(period.startDate) : period.startDate != null) return false;
        return !(endDate != null ? !endDate.equals(period.endDate) : period.endDate != null);

    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return DateUtil.format(startDate) + " ~ " + DateUtil.format(endDate);
    }
}
