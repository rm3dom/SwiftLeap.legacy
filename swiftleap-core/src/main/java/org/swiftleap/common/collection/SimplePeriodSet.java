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
import java.util.stream.Stream;

/**
 * Created by ruans on 2016/02/13.
 */
public class SimplePeriodSet<E extends EffectivePeriod> implements PeriodSet<E>, Set<E> {

    SortedSet<Entry> startSet = null;
    SortedSet<Entry> endSet = null;

    public SimplePeriodSet() {
        startSet = new TreeSet<>(EffectivePeriod.START_ASC);
        endSet = new TreeSet<>(EffectivePeriod.END_ASC);
    }

    public SimplePeriodSet(Iterable<? extends E> c) {
        startSet = new TreeSet<>(EffectivePeriod.START_ASC);
        endSet = new TreeSet<>(EffectivePeriod.END_ASC);
        c.forEach(this::add);
    }

    public SimplePeriodSet(Stream<? extends E> c) {
        startSet = new TreeSet<>(EffectivePeriod.START_ASC);
        endSet = new TreeSet<>(EffectivePeriod.END_ASC);
        c.forEach(this::add);
    }

    protected SimplePeriodSet(SortedSet<Entry> fromSet, SortedSet<Entry> endSet) {
        this.startSet = fromSet;
        this.endSet = endSet;
    }

    @SuppressWarnings("unchecked")
    List<Entry> toList(Collection<?> c) {
        List<Entry> ret = new ArrayList<>(c.size());
        c.forEach(e -> ret.add(new Entry((E) e)));
        return ret;
    }

    @Override
    public int size() {
        return startSet.size();
    }

    @Override
    public boolean isEmpty() {
        return startSet.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return startSet.contains(new Entry((E) o));
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<Entry> it = startSet.iterator();
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @SuppressWarnings("unchecked")
            @Override
            public E next() {
                return (E) it.next().entry;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] a = new Object[startSet.size()];
        int count = 0;
        for (Entry e : startSet) {
            a[count++] = e.entry;
        }
        return a;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        T[] r = a;
        if (a.length < size()) {
            r = (T[]) java.lang.reflect.Array
                    .newInstance(a.getClass().getComponentType(), size());
            System.arraycopy(a, 0, r, 0, a.length);
        }
        int count = 0;
        for (Entry e : startSet) {
            r[count++] = (T) e.entry;
        }
        return r;
    }

    @Override
    public boolean add(E e) {
        Entry n = new Entry(e);
        return startSet.add(n) && endSet.add(n);
    }

    protected boolean add(Entry n) {
        return startSet.add(n) && endSet.add(n);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        Entry n = new Entry((E) o);
        return startSet.remove(n) && endSet.remove(n);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsAll(Collection<?> c) {
        List<Entry> l = toList(c);
        return startSet.containsAll(l);
    }

    @Override
    public SimplePeriodSet<E> append(Collection<? extends E> a) {
        if (a == null) return this;
        SimplePeriodSet<E> ret = new SimplePeriodSet<>();
        ret.addAll(this);
        ret.addAll(a);
        return ret;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        List<Entry> l = toList(c);
        return startSet.addAll(l) && endSet.addAll(l);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        List<Entry> l = toList(c);
        return startSet.removeAll(l) && endSet.removeAll(l);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<Entry> l = toList(c);
        return startSet.retainAll(l) && endSet.retainAll(l);
    }

    @Override
    public void clear() {
        startSet.clear();
        endSet.clear();
    }

    public PeriodSet<E> effectiveToday() {
        return effective(new Date());
    }

    public PeriodSet<E> effective(Date effectiveDate) {
        if (effectiveDate == null) return EMPTY;
        //Headset returns smaller, but we want smaller or equal too, so we add one day
        Date testDate = DateUtil.addOneDay(effectiveDate);
        SimplePeriodSet<E> ret = new SimplePeriodSet<>();
        for (Entry e : startSet.headSet(new Entry(testDate))) {
            if (DateUtil.contains(e, effectiveDate)) {
                ret.add(e);
                continue;
            }
            //If the start date is greater we can break as its sorted
            if (DateUtil.dateCompare(e.getStartDate(), effectiveDate) > 0) {
                break;
            }
        }
        return ret;
    }

    /**
     * All periods before and to toElement, ie the end date is smaller or equal too.
     *
     * @param toDate
     * @return
     */
    public PeriodSet<E> headSet(Date toDate) {
        if (toDate == null) return this;
        SortedSet<Entry> s = endSet.headSet(new Entry(toDate));
        SortedSet<Entry> ss = new TreeSet<>(EffectivePeriod.START_ASC);
        SortedSet<Entry> es = new TreeSet<>(EffectivePeriod.END_ASC);
        ss.addAll(s);
        es.addAll(s);
        return new SimplePeriodSet<>(ss, es);
    }

    /**
     * All periods from fromElement onwards, ie the start date is greater or equal too.
     *
     * @param fromDate
     * @return
     */
    public PeriodSet<E> tailSet(Date fromDate) {
        if (fromDate == null) return this;
        SortedSet<Entry> s = startSet.tailSet(new Entry(fromDate));
        SortedSet<Entry> ss = new TreeSet<>(EffectivePeriod.START_ASC);
        SortedSet<Entry> es = new TreeSet<>(EffectivePeriod.END_ASC);
        ss.addAll(s);
        es.addAll(s);
        return new SimplePeriodSet<>(ss, es);
    }

    @SuppressWarnings("unchecked")
    public E first() {
        if (startSet.isEmpty()) return null;
        return (E) startSet.first().entry;
    }

    @SuppressWarnings("unchecked")
    public E last() {
        if (endSet.isEmpty()) return null;
        return (E) endSet.last().entry;
    }

    @SuppressWarnings("unchecked")
    public void walkStart(Consumer<E> c) {
        startSet.forEach(e -> c.accept((E) e.entry));
    }

    @SuppressWarnings("unchecked")
    public void walkEnd(Consumer<E> c) {
        endSet.forEach(e -> c.accept((E) e.entry));
    }

    @Override
    public PeriodSet<Period> gaps() {
        SimplePeriodSet<Period> ret = new SimplePeriodSet<>();
        //Can not have gaps with 1 or less periods.
        if (size() < 2) return ret;
        Date start = null;
        Calendar c = Calendar.getInstance();
        for (Entry entry : startSet) {
            if (!DateUtil.isValid(entry)) continue;
            if (start != null) {
                //The next period starts here and we have a start date
                //which is not contained in any period
                c.setTime(entry.getStartDate());
                c.add(Calendar.DAY_OF_YEAR, -1);
                Period p = new Period(start, c.getTime());
                if (p.isValid()) {
                    ret.add(p);
                }
                start = null;
            }
            c.setTime(entry.getEndDate());
            c.add(Calendar.DAY_OF_YEAR, 1);
            Date d = c.getTime();
            if (!effective(d).isEmpty()) continue;
            //There is no period which contains this date
            start = d;
        }
        return ret;
    }

    @Override
    public PeriodSet<E> overlaps() {
        SimplePeriodSet<E> ret = new SimplePeriodSet<>();
        for (Entry entry : startSet) {
            PeriodSet<E> s = effective(entry.getEndDate());
            //We have more than one entry, add them all including this one.
            if (s.size() > 1) s.forEach(ret::add);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PeriodSet<E> overlapping(EffectivePeriod period) {
        SimplePeriodSet<E> ret = new SimplePeriodSet<>();
        for (Entry entry : startSet) {
            if (DateUtil.overlaps(period, entry) || DateUtil.overlaps(entry, period)) {
                ret.add((E) entry.entry);
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PeriodSet<E> valid() {
        SimplePeriodSet<E> ret = new SimplePeriodSet<>();
        for (Entry entry : startSet) {
            if (!DateUtil.isValid(entry)) continue;
            ret.add((E) entry.entry);
        }
        return ret;
    }

    @Override
    public PeriodSet<Period> intersection(PeriodSet<?> other) {
        SimplePeriodSet<Period> ret = new SimplePeriodSet<>();
        for (EffectivePeriod left : this) {
            if (!DateUtil.isValid(left)) continue;
            for (EffectivePeriod right : other) {
                if (!DateUtil.isValid(right)) continue;
                //In case we are testing the set against itself, do not compare the same objects
                if (this == other && left == right) continue;

                //We can break here
                //1) [   ][  ][   ]
                //      [      ][   ]
                //2)   [  ][  ][     ]
                //      [    ]
                if (DateUtil.dateCompare(left.getEndDate(), right.getStartDate()) < 0) break;

                Period p = DateUtil.intersection(left, right);
                if (!p.isValid()) {
                    continue;
                }
                ret.add(p);
            }
        }
        return ret;
    }

    @Override
    public PeriodSet<Period> difference(PeriodSet<?> other) {
        //TODO NOTE There must be a faster and more elegant way.

        List<Period> ret = new ArrayList<>();

        //Differences between the two sets
        List<Period> diff = new ArrayList<>(2);
        //Differences to grow by
        List<Period> diffAdd = new ArrayList<>(2);

        for (EffectivePeriod left : this) {
            if (!DateUtil.isValid(left)) continue;
            diff.clear();
            diff.add(new Period(left));
            for (EffectivePeriod right : other) {
                if (!DateUtil.isValid(right)) continue;

                //We can break here
                //1) [   ][  ][   ]
                //      [      ][   ]
                //2)   [  ][  ][     ]
                //      [    ]
                if (DateUtil.dateCompare(left.getEndDate(), right.getStartDate()) < 0) break;

                // [-----------]
                //    [----][-----]
                // [-]

                // [-----------]
                //    [----]
                // [-]      [--]

                // [-----------]
                //          [----]
                // [-------]

                //Loop over the diff continuously to cut the period
                diffAdd.clear();
                Iterator<Period> iter = diff.iterator();
                while (iter.hasNext()) {
                    Period period = iter.next();

                    //Fully contained, remove it
                    if (DateUtil.contains(right, period)) {
                        iter.remove();
                        continue;
                    }

                    //It overlaps, cut it and add it
                    if (period.overlaps(right)) {
                        //Remove it, its being cut
                        iter.remove();

                        if (DateUtil.dateCompare(period.getStartDate(), right.getStartDate()) < 0) {
                            diffAdd.add(new Period(period.getStartDate(), DateUtil.subtractOneDay(right.getStartDate())));
                        }

                        if (DateUtil.dateCompare(period.getEndDate(), right.getEndDate()) > 0) {
                            diffAdd.add(new Period(DateUtil.addOneDay(right.getEndDate()), period.getEndDate()));
                        }
                    }
                }
                diff.addAll(diffAdd);
            }


            //Grow the ones that overlap and finally add whats left
            Iterator<Period> iter = diff.iterator();
            while (iter.hasNext()) {
                Period p = iter.next();
                for (Period right : ret) {
                    if (right.overlaps(p)) {
                        right.grow(p);
                        iter.remove();
                    }
                }
            }
            ret.addAll(diff);
        }
        return PeriodSet.from(ret);
    }

    @Override
    public PeriodSet<Period> slice(SortedSet<Date> startDates) {
        SimplePeriodSet<Period> ret = new SimplePeriodSet<>();
        for (EffectivePeriod left : this) {
            Period period = new Period(left);
            for (Date startDate : startDates) {
                if (DateUtil.contains(period, startDate)
                        && DateUtil.dateCompare(period.getStartDate(), startDate) != 0) {
                    ret.add(new Period(period.getStartDate(), DateUtil.subtractOneDay(startDate)));
                    period = new Period(startDate, period.getEndDate());
                }
            }
            ret.add(period);
        }
        return ret;
    }

    @Override
    public Stream<E> stream() {
        return Set.super.stream();
    }

    class Entry implements EffectivePeriod {
        EffectivePeriod entry;

        public Entry(Date date) {
            entry = new Period(date, date);
        }

        public Entry(EffectivePeriod entry) {
            this.entry = entry;
        }

        @Override
        public Date getEndDate() {
            return entry.getEndDate();
        }

        @Override
        public Date getStartDate() {
            return entry.getStartDate();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            return this == o || entry.equals(((Entry) o).entry);
        }

        @Override
        public int hashCode() {
            return entry.hashCode();
        }

        @Override
        public String toString() {
            return entry.toString();
        }
    }
}

