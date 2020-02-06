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
package org.swiftleap.common.persistance.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swiftleap.common.persistance.SequenceGenerator;
import org.swiftleap.common.types.Range;

/**
 * Created by ruans on 2017/06/04.
 */
@Service
public class SequenceGeneratorImpl implements SequenceGenerator {
    @Autowired
    TenantSequenceDao tenantSequenceDao;
    @Autowired
    SequenceDao sequenceDao;

    TenantSequenceDbo getTenantedSequence(String name) {
        TenantSequenceDbo seq = tenantSequenceDao.findByName(name);
        if (seq == null) {
            seq = new TenantSequenceDbo();
            seq.setName(name);
            seq.setNextSequence(1);
            seq = tenantSequenceDao.persist(seq);
        }
        tenantSequenceDao.lock(seq);
        return seq;
    }

    @Override
    public Range getTenantedSequence(String name, int count) {
        TenantSequenceDbo seq = getTenantedSequence(name);
        int start = seq.getNextSequence();
        seq.setNextSequence(start + count);
        return new Range(start, count);
    }

    @Override
    public void ensureTenantedSequence(String name, int value) {
        TenantSequenceDbo seq = getTenantedSequence(name);
        seq.setNextSequence(Math.max(value, seq.getNextSequence()));
    }


    SequenceDbo getSequence(String name) {
        SequenceDbo seq = sequenceDao.findByName(name);
        if (seq == null) {
            seq = new SequenceDbo();
            seq.setName(name);
            seq.setNextSequence(1);
            seq = sequenceDao.persist(seq);
        }
        sequenceDao.lock(seq);
        return seq;
    }


    @Override
    public Range getSequence(String name, int count) {
        SequenceDbo seq = getSequence(name);
        int start = seq.getNextSequence();
        seq.setNextSequence(start + count);
        return new Range(start, count);
    }

    @Override
    public void ensureSequence(String name, int value) {
        SequenceDbo seq = getSequence(name);
        seq.setNextSequence(Math.max(value, seq.getNextSequence()));
    }
}
