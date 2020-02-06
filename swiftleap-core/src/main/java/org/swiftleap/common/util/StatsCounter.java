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

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.*;

public class StatsCounter {

    public static final Metric ZERO;

    static {
        DefaultMetric zero = new DefaultMetric();
        zero.setMinMillis(0);
        ZERO = zero;
    }

    private long numBuckets;
    private Duration counterTimerSpan;
    private HashMap<String, Counter> counters = new HashMap<>();

    public StatsCounter(Duration counterTimerSpan, long numBuckets) {
        this.numBuckets = numBuckets;
        this.counterTimerSpan = counterTimerSpan;

    }

    public void increment(String key, Status status, long startMillis, long millis) {
        Counter counter;
        synchronized (this) {
            counter = counters.get(key);
            if (counter == null) {
                counter = new Counter(key, startMillis, counterTimerSpan, numBuckets);
                counters.put(key, counter);
            }
        }

        counter.increment(status, startMillis, millis);
    }

    public Map<String, Metric> total(long start, Duration duration) {
        synchronized (this) {
            return counters
                    .values()
                    .stream()
                    .reduce((Map<String, Metric>) new HashMap<String, Metric>(),
                            (dict, counter) -> Collections.singletonMap(counter.getKey(), counter.total(start, duration)),
                            (l, r) -> {
                                l.putAll(r);
                                return l;
                            });
        }
    }

    public Map<String, Metric> total() {
        synchronized (this) {
            return counters
                    .values()
                    .stream()
                    .reduce((Map<String, Metric>) new HashMap<String, Metric>(),
                            (dict, counter) -> Collections.singletonMap(counter.getKey(), counter.total()),
                            (l, r) -> {
                                l.putAll(r);
                                return l;
                            });
        }
    }

    public enum Status {
        SUCCESS,
        ERROR
    }

    public interface Metric {
        default double getTotalCount() {
            return getSuccessful() + getErrors();
        }

        default double getAvgMillis() {
            return getTotalCount() < 1 ? 0 : getTotalMillis() / getTotalCount();
        }

        double getTotalMillis();

        double getMaxMillis();

        double getMinMillis();

        double getSuccessful();

        double getErrors();

        default Metric add(Metric other) {
            DefaultMetric ret = new DefaultMetric();
            ret.setErrors(getErrors() + other.getErrors());
            ret.setSuccessful(getSuccessful() + other.getSuccessful());
            ret.setMaxMillis(Math.max(getMaxMillis(), other.getMaxMillis()));
            ret.setMinMillis(Math.min(getMinMillis(), other.getMinMillis()));
            ret.setTotalMillis(getTotalMillis() + other.getTotalMillis());
            return ret;
        }
    }

    @Setter
    @Getter
    private static class DefaultMetric implements Metric {

        double successful;
        double errors;
        double maxMillis;
        double minMillis = Double.MAX_VALUE;
        double totalMillis;
    }

    @Getter
    @Setter
    private static class Bucket implements Metric {
        long startTime;
        double successful;
        double errors;
        double maxMillis;
        double minMillis = Double.MAX_VALUE;
        double totalMillis;

        Bucket(long startTime) {
            this.startTime = startTime;
        }

        public void increment(Status status, long millis) {
            increment(status, millis, 1D);
        }

        public void increment(Status status, long millis, double portion) {
            totalMillis += millis;
            //Taking a fraction of the duration for the max and min does not make sense
            //Divide the fraction to get the original duration.
            maxMillis = Math.max(maxMillis, (double) millis / portion);
            minMillis = Math.min(minMillis, (double) millis / portion);

            if (status == Status.ERROR)
                errors += portion;
            else
                successful += portion;
        }
    }

    @Getter
    @Setter
    private static class Counter {
        LinkedList<Bucket> buckets = new LinkedList<>();
        long startTime;
        String key;
        long bucketDuration;
        long numBuckets;

        public Counter(String key, long startTime, Duration timeSpan, long numBuckets) {
            this.startTime = startTime;
            this.key = key;
            this.bucketDuration = timeSpan.toMillis() / numBuckets;
            this.numBuckets = numBuckets;
            buckets.add(new Bucket(startTime));
        }

        public void increment(Status status, long start, long millis) {
            increment(status, start, millis, 1D);
        }

        public void increment(Status status, long start, long millis, double portion) {
            //This is what 'calls / increments' over multiple buckets could look like.
            //|-----------------||-----------------||-----------------|
            //       <--------------------------------->
            //       <------>
            //
            // First one above is distributed over 3 buckets: 30%, 50%, 20%
            // The next one is 100% in one bucket.

            Bucket bucket = null;
            synchronized (this) {
                Iterator<Bucket> iter = buckets.iterator();
                while (iter.hasNext()) {
                    bucket = iter.next();

                    if (bucket.getStartTime() + getBucketDuration() <= start) {
                        //Calculate the new start date instead of adding multiple buckets until we reach the start date.
                        long rest = (getStartTime() - start) % getBucketDuration();
                        long bucketStart = start + rest;
                        bucket = new Bucket(bucketStart);
                        buckets.addFirst(bucket);
                        if (buckets.size() > getNumBuckets())
                            buckets.removeLast();
                        break;
                    }

                    if (bucket.getStartTime() > start)
                        continue;

                    //This bucket is good.
                    break;
                }
            }

            long duration = getBucketDuration() - (start - bucket.getStartTime());

            if (millis - duration > 0) {
                //Keep splitting 'millis' into multiple buckets until the all of 'millis' has been distributed.
                //Portion in this case could be something like 0.4 as 'millis' is only 40% of the original 'millis'
                double fraction = portion * ((double) duration / millis);
                increment(status, start, duration, fraction);
                increment(status, start + duration, millis - duration, portion - fraction);
                return;
            }

            synchronized (this) {
                bucket.increment(status, millis, portion);
            }
        }

        public Metric total(long start, Duration duration) {
            long end = start + duration.toMillis();

            synchronized (this) {
                //TODO Add should only add a portion of the duration and not the whole bucket
                //For now this is not a problem because we will always ask for the last buckets
                return buckets
                        .stream()
                        .filter(b -> b.getStartTime() >= start && b.getStartTime() < end)
                        .reduce(new DefaultMetric(), Metric::add, (m1, m2) -> m2);
            }
        }

        public Metric total() {
            synchronized (this) {
                Metric ret = buckets
                        .stream()
                        .reduce(new DefaultMetric(), Metric::add, (m1, m2) -> m2);
                return ret;
            }
        }
    }

}
