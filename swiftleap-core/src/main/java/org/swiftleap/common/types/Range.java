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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ruan on 2015/08/10.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@Data
public class Range {
    int start = -1;
    int end = -1;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean isInRange(long v) {
        return start <= v && end >= v;
    }

    public boolean isBetween(long v) {
        return isInRange(v);
    }

    public boolean isValid() {
        return start <= end && start >= 0;
    }

    public int getCount() {
        return end - start;
    }
}
