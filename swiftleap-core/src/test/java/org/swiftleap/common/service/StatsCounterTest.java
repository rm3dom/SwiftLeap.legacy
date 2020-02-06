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

import org.junit.Test;
import org.swiftleap.common.util.StatsCounter;

import java.time.Duration;

public class StatsCounterTest {

    @Test
    public void total() {
        long time = System.currentTimeMillis();
        StatsCounter mon = new StatsCounter(Duration.ofDays(1), 24 * 60);

        mon.increment("test", StatsCounter.Status.SUCCESS, time, Duration.ofSeconds(150).toMillis());
        //mon.increment("test", StatsCounter.Status.SUCCESS, time + Duration.ofSeconds(80).toMillis(), Duration.ofSeconds(90).toMillis());

        StatsCounter.Metric metric = mon.total().get("test");


        System.out.println("" + metric.getTotalMillis() + " " + metric.getAvgMillis());

    }
}