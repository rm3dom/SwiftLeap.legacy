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
package org.swiftleap.common.email.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swiftleap.common.email.AbstractMailHandler;
import org.swiftleap.common.email.Conf;
import org.swiftleap.common.email.HandlerException;

import javax.mail.Flags;
import javax.mail.Message;


@Component
public class SwiftLeapMailHandler extends AbstractMailHandler {

    @Autowired
    SwiftLeapMailConfig conf;

    @Override
    protected Flags.Flag[] handle(Object mailMessage) throws HandlerException {
        throw new HandlerException("Not supported");
    }

    @Override
    protected Object prepareOrIgnore(Message message) throws HandlerException {
        throw new HandlerException("Not supported");
    }

    @Override
    public Conf getConf() {
        return conf;
    }

    @Override
    protected void processInbox() throws Exception {
        //nothing
    }
}
