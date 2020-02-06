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

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for GUI grids.
 * <p>
 * Created by ruans on 2015/08/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class PagingResult<T> {
    int totalResults;
    Range range;
    List<? extends T> results = new ArrayList<>(0);

    public PagingResult() {
    }

    public PagingResult(int totalResults, Range range, List<? extends T> results) {
        this.totalResults = totalResults;
        this.range = range;
        this.results = results;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<? extends T> getResults() {
        return results;
    }

    public void setResults(List<? extends T> results) {
        this.results = results;
    }
}
