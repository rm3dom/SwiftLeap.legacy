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
package org.swiftleap.common.util;


import org.swiftleap.common.types.EffectivePeriod;
import org.swiftleap.common.types.Period;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ruan on 2015/08/10.
 */
public class DateUtil {

    public static Date START_OF_TIME = DateUtil.makeStartOfDay(DateUtil.make(1900, 0, 1));
    public static Date END_OF_TIME = DateUtil.makeStartOfDay(DateUtil.make(9999, 11, 31));

    private static void setToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static Date subtractOneDay(Date date) {
        return addDays(date, -1);
    }

    public static Date addOneDay(Date date) {
        return addDays(date, 1);
    }

    public static Date addDays(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }


    public static Date makeStartOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setToStartOfDay(calendar);
        return calendar.getTime();
    }

    public static Date makeEndOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setToEndOfDay(calendar);
        return calendar.getTime();
    }

    private static boolean isTimeSep(char c) {
        return c == ':' || c == 'h' || c == 'H';
    }

    /**
     * @param anyForm date
     * @return new date instance initialized to the start of the day i.e. hour = 0, minutes = 0, seconds = 0, ms = 0
     */
    public static Date make(String anyForm) {
        if (anyForm == null || anyForm.isEmpty()) {
            return null;
        }

        anyForm = anyForm.trim();

        int year = 1900;
        int month = 0;
        int day = 1;
        int hour = 0;
        int min = 0;
        int sec = 0;

        if (anyForm.length() == 10) {
            String[] splits = anyForm.split("[\\-\\\\/. ]");

            if (splits.length < 3)
                throw new IllegalArgumentException("Invalid date format: " + anyForm);

            if (splits[2].length() == 4) {
                splits = ArrayUtil.flip(new String[splits.length], splits);
            }

            year = Integer.parseInt(splits[0]);
            month = Integer.parseInt(splits[1]) - 1;
            day = Integer.parseInt(splits[2]);
        } else if (anyForm.length() == 8 && isTimeSep(anyForm.charAt(2)) && isTimeSep(anyForm.charAt(5))) {
            hour = Integer.parseInt(anyForm.substring(0, 2));
            min = Integer.parseInt(anyForm.substring(3, 5));
            sec = Integer.parseInt(anyForm.substring(6, 8));
        }
        else if (anyForm.length() == 8) {
            year = Integer.parseInt(anyForm.substring(0, 4));
            month = Integer.parseInt(anyForm.substring(4, 6)) - 1;
            day = Integer.parseInt(anyForm.substring(6, 8));
        } else if(anyForm.length() <= 2) {
            min = Integer.parseInt(anyForm);
        }
        else if(anyForm.length() == 3) {
            hour = Integer.parseInt(anyForm.substring(0, 1));
            min = Integer.parseInt(anyForm.substring(1, 3));
        }
        else if(anyForm.length() == 4 && isTimeSep(anyForm.charAt(1))) {
            hour = Integer.parseInt(anyForm.substring(0, 1));
            min = Integer.parseInt(anyForm.substring(2, 4));
        }
        else if(anyForm.length() == 4) {
            hour = Integer.parseInt(anyForm.substring(0, 2));
            min = Integer.parseInt(anyForm.substring(2, 4));
        }
        else if (anyForm.length() == 5 && isTimeSep(anyForm.charAt(2))) {
            hour = Integer.parseInt(anyForm.substring(0, 2));
            min = Integer.parseInt(anyForm.substring(3, 5));
        } else  {
            TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(anyForm);
            Instant i = Instant.from(ta);
            return Date.from(i);
        }

        return make(year, month, day, hour, min, sec);
    }

    /**
     * @param yyyymmdd date
     * @return new date instance initialized to the start of the day i.e. hour = 0, minutes = 0, seconds = 0, ms = 0
     */
    public static Date make(int yyyymmdd) {
        int year = yyyymmdd / 10000;
        int month = (yyyymmdd / 100) % 100 - 1;
        int day = yyyymmdd % 100;
        return make(year, month, day);
    }

    /**
     * @param year
     * @param month 0 is the first month
     * @param day   1 is the first day
     * @return new date instance initialized to the start of the day i.e. hour = 0, minutes = 0, seconds = 0, ms = 0
     */
    public static Date make(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        try {
            setToStartOfDay(calendar);
            calendar.set(year, month, day);
            return calendar.getTime();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date parameters specified: year = " + year + " month = " + month + " day = " + day);
        }
    }

    /**
     * @param year
     * @param month 0 is the first month
     * @param day   1 is the first day
     * @return new date instance initialized to the start of the day i.e. hour = 0, minutes = 0, seconds = 0, ms = 0
     */
    public static Date make(int year, int month, int day, int hour, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        try {
            setToStartOfDay(calendar);
            calendar.set(year, month, day, hour, min, sec);
            return calendar.getTime();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date parameters specified: year = " + year + " month = " + month + " day = " + day);
        }
    }

    public static String formatLegacy(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(d);
    }

    public static String formatAus(Date effDate) {
        if (effDate == null) return null;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(effDate);
    }

    public static String format(Date effDate) {
        if (effDate == null) return null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(effDate);
    }

    public static boolean contains(EffectivePeriod left, EffectivePeriod right) {
        return (dateCompare(left.getStartDate(), right.getStartDate()) <= 0 &&
                dateCompare(left.getEndDate(), right.getEndDate()) >= 0);
    }

    public static boolean contains(EffectivePeriod left, Date right) {
        return (dateCompare(left.getStartDate(), right) <= 0 &&
                dateCompare(left.getEndDate(), right) >= 0);
    }

    public static boolean isContinuous(EffectivePeriod left, EffectivePeriod right) {
        //if they overlap or lie right next to each over they are continuous
        boolean overlaps = overlaps(left, right);
        if (overlaps) return true;
        int days = getDaysBetween(left.getStartDate(), right.getEndDate());
        if (days == 0) return true;
        days = getDaysBetween(left.getEndDate(), right.getStartDate());
        return days == 0;
    }

    public static boolean overlaps(EffectivePeriod left, EffectivePeriod right) {
        return contains(left, right.getStartDate()) || contains(left, right.getEndDate());
    }

    public static Date min(Date left, Date right) {
        return left.compareTo(right) < 0 ? left : right;
    }

    public static Date max(Date left, Date right) {
        return left.compareTo(right) > 0 ? left : right;
    }

    public static LocalDate toLocalDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return LocalDateTime.of(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
    }

    public static Period intersection(EffectivePeriod left, EffectivePeriod right) {
        return new Period(max(left.getStartDate(), right.getStartDate()), min(left.getEndDate(), right.getEndDate()));
    }

    public static int dateCompare(Date left, Date right) {
        if (left == right) return 0;
        if (right == null) return 1;
        if (left == null) return -1;
        return toLocalDate(left).compareTo(toLocalDate(right));
    }

    public static boolean isValid(EffectivePeriod p) {
        return p.getStartDate() != null && p.getEndDate() != null && p.getStartDate().compareTo(p.getEndDate()) < 1;
    }

    public static int getDaysBetween(Date startDate, Date endDate) {
        return (int) ChronoUnit.DAYS.between(toLocalDate(startDate), toLocalDate(endDate));
    }

    public static int getYearBetween(Date startDate, Date endDate) {
        return (int) ChronoUnit.YEARS.between(toLocalDate(startDate), toLocalDate(endDate));
    }
}
