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
package org.swiftleap.common.collection;


import org.swiftleap.common.types.EffectivePeriod;
import org.swiftleap.common.types.Period;
import org.swiftleap.common.util.DateUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A readonly EffectivePeriod set.
 * This must always be an interface and always immutable.
 * Created by ruans on 2016/02/11.
 */
public interface PeriodSet<E extends EffectivePeriod> extends Iterable<E> {
    PeriodSet EMPTY = new SimplePeriodSet();

    static <T extends EffectivePeriod> PeriodSet<T> emptySet(Class<T> clazz) {
        return EMPTY;
    }

    static <T extends EffectivePeriod> PeriodSet<T> from(PeriodSet<T> c) {
        //Do not copy its immutable
        return c;
    }

    static <T extends EffectivePeriod> PeriodSet<T> from(Iterable<T> c) {
        if (c == null) return new SimplePeriodSet<>();
        return new SimplePeriodSet<>(c);
    }

    static <T extends EffectivePeriod> PeriodSet<T> from(Stream<T> c) {
        if (c == null) return new SimplePeriodSet<>();
        return new SimplePeriodSet<>(c);
    }

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    boolean contains(Object o);

    Object[] toArray();

    <T> T[] toArray(T[] a);

    boolean containsAll(Collection<?> c);

    /**
     * All entries effective today:
     * <p>
     * <pre>
     * Example: [----][----][----]
     *                  ^ today.
     * </pre>
     *
     * @return
     */
    PeriodSet<E> effectiveToday();

    default List<E> list() {
        ArrayList<E> l = new ArrayList<>(size());
        forEach(l::add);
        return l;
    }

    default PeriodSet<E> append(PeriodSet<? extends E> a) {
        return append(a.list());
    }

    default PeriodSet<E> append(E... a) {
        return append(Arrays.asList(a));
    }


    PeriodSet<E> append(Collection<? extends E> a);


    /**
     * Find all periods effective at some date.
     * <p>
     * <pre>
     *        Effective
     *           \/
     *    [-------------]
     *       [--------------][---------------]
     * </pre>
     *
     * @param effectiveDate the effective date, if null does nothing.
     * @return a set with all periods enclosing effectiveDate.
     */
    PeriodSet<E> effective(Date effectiveDate);

    /**
     * All periods before and to toElement, ie the end date is smaller or equal too.
     * <p>
     * <pre>
     * Example: [----][------------]<<toDate<<[--].
     * </pre>
     *
     * @param toDate the maximum date, if null does nothing.
     * @return
     */
    PeriodSet<E> headSet(Date toDate);

    /**
     * All periods from fromDate onwards, ie the start date is greater or equal too.
     * <p>
     * <pre>
     * Example: [----][------------]>>fromDate>>[--].
     * </pre>
     *
     * @param fromDate the minimum date, if null does nothing.
     * @return
     */
    PeriodSet<E> tailSet(Date fromDate);

    /**
     * Return all gaps in the set.
     * <p>
     * <pre>
     * Example: [----][gap][------------][gap][--].
     * </pre>
     *
     * @return
     */
    PeriodSet<Period> gaps();

    /**
     * Return all entries that overlap each other.
     * <p>
     * <pre>
     * Example, 2 overlap:
     * [------]       [------]
     *      [------]
     * </pre>
     *
     * @return
     */
    PeriodSet<E> overlaps();


    /**
     * Intersection.
     * <p>
     * <pre>
     * Example:
     * Set1:   [------]       [------]
     * Set2:      [------]
     * Result:    [---]
     * </pre>
     *
     * @return
     * @see PeriodSet#difference(PeriodSet)
     */
    PeriodSet<Period> intersection(PeriodSet<?> other);


    /**
     * Difference.
     * <p>
     * <pre>
     * Example:
     * Set1:   [------]       [--------]
     * Set2:      [------]       [--]
     * Result: [-]            [-]    [-]
     * </pre>
     *
     * @return
     * @see PeriodSet#intersection(PeriodSet)
     */
    PeriodSet<Period> difference(PeriodSet<?> other);

    /**
     * Slice the periods.
     * <pre>
     *         |
     *    [--------]
     *         |
     *    [--- ][--]
     *         |
     * </pre>
     *
     * @return
     */
    default PeriodSet<Period> slice(Date... startDates) {
        return slice(Arrays.asList(startDates));
    }

    /**
     * Slice on a period's edges, the end date will have one day added to it.
     *
     * @param period
     * @return
     */
    default PeriodSet<Period> slice(Period period) {
        return slice(period.getStartDate(), DateUtil.addOneDay(period.getEndDate()));
    }

    PeriodSet<Period> slice(SortedSet<Date> dates);

    default PeriodSet<Period> slice(Collection<Date> dates) {
        return slice(new TreeSet<>(dates));
    }

    default PeriodSet<Period> slice(Stream<Date> dates) {
        return slice(dates.collect(Collectors.toList()));
    }

    /**
     * All entries overlapping the period.
     * <pre>
     * Example, these all overlap:
     *     [---------]
     * [-----------------]
     *     [---------]
     *             [--------]
     * </pre>
     *
     * @param period
     * @return
     */
    PeriodSet<E> overlapping(EffectivePeriod period);


    /**
     * Valid entries only.
     * Valid entries are start date &lt;= end date.
     * <pre>
     *     Example:
     *        ][
     * </pre>
     *
     * @return
     */
    PeriodSet<E> valid();

    E first();

    default Optional<E> firstOptional() {
        return Optional.ofNullable(first());
    }

    E last();

    default Optional<E> lastOptional() {
        return Optional.ofNullable(last());
    }

    void walkStart(Consumer<E> c);

    void walkEnd(Consumer<E> c);

    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * An ordered list of interesting dates, ie all start and end dates.
     *
     * @return a list of interesting dates
     */
    default SortedSet<Date> interestingDates() {
        return interestingDates(false);
    }


    /**
     * An ordered list of interesting dates, ie all start and end dates.
     *
     * @param addOneDayToEndDate if true one day is added to the endDate
     * @return a list of interesting dates
     */
    default SortedSet<Date> interestingDates(boolean addOneDayToEndDate) {
        if (addOneDayToEndDate) {
            return stream().flatMap(e -> Stream.of(e.getStartDate(), DateUtil.addOneDay(e.getEndDate())))
                    .sorted()
                    .distinct()
                    .collect(Collectors.toCollection(TreeSet::new));
        } else {
            return stream().flatMap(e -> Stream.of(e.getStartDate(), e.getEndDate()))
                    .sorted()
                    .distinct()
                    .collect(Collectors.toCollection(TreeSet::new));
        }
    }
}
