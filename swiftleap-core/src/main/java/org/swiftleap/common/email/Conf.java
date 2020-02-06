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
package org.swiftleap.common.email;

import org.swiftleap.common.types.EnumValueType;

/**
 * Created by ruan on 2015/06/04.
 */
public interface Conf {
    int getProcessSeconds();

    String getChannelName();

    String getEmailAddress();

    String getEmailUser();

    String getEmailPassword();

    Protocol getReceiveProtocol();

    String getReceiveHost();

    int getReceivePort();

    boolean isReceiveTLS();

    String getSmtpHost();

    int getSmtpPort();

    boolean isSmtpTLS();

    enum Protocol implements EnumValueType<String> {
        IMAP("imap"),
        IMAPS("imaps"),
        POP("pop");

        String value;


        Protocol(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
