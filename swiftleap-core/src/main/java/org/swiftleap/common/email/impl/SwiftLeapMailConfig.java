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

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.swiftleap.common.email.Conf;

/**
 * Created by ruans on 2017/12/05.
 */
@Component
@Getter
@Setter
public class SwiftLeapMailConfig implements Conf {
    int processSeconds = 30;
    String channelName = "swiftleap";
    @Value("${email.address}")
    String emailAddress;
    @Value("${email.user}")
    String emailUser;
    @Value("${email.password}")
    String emailPassword;
    @Value("${email.rcv.proto}")
    Conf.Protocol receiveProtocol;
    @Value("${email.rcv.host}")
    String receiveHost;
    @Value("${email.rcv.port}")
    int receivePort;
    @Value("${email.rcv.tls}")
    boolean receiveTLS;
    @Value("${email.smtp.host}")
    String smtpHost;
    @Value("${email.smtp.port}")
    int smtpPort;
    @Value("${email.smtp.tls}")
    boolean smtpTLS;
}
