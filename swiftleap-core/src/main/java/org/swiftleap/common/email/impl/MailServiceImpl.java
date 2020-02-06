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
import org.springframework.stereotype.Service;
import org.swiftleap.common.email.HandlerException;
import org.swiftleap.common.email.MailHandler;
import org.swiftleap.common.email.MailService;
import org.swiftleap.common.email.SimpleMailMessage;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruans on 2017/11/27.
 */
@Service
public class MailServiceImpl implements MailService {

    Map<String, MailHandler> handlers = new HashMap<>();

    @Autowired(required = false)
    public void setHandlerList(List<MailHandler> handlerList) {
        handlerList.forEach(handler -> {
            handlers.put(handler.getConf().getChannelName(), handler);
        });
    }

    @PostConstruct
    public synchronized void start() {
        handlers.values().forEach(MailHandler::start);
    }

    public synchronized void stop() {
        handlers.values().forEach(MailHandler::stop);
    }

    @Override
    public void send(String channel, SimpleMailMessage simpleMessage) throws HandlerException {
        //TODO add to queue, then read from queue transactionaly
        MailHandler handler = handlers.get(channel);
        if (handler == null)
            handler = handlers.get("default");
        if (handler == null)
            throw new HandlerException("There is no channel: " + channel + " or default channel available");
        if (!handler.isRunning()) {
            throw new HandlerException("The mail handler is not running");
        }
        handler.send(simpleMessage);
    }
}
