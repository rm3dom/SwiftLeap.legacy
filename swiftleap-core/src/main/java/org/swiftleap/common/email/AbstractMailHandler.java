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

import lombok.val;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.EmailException;
import org.swiftleap.common.email.impl.SmtpUtil;
import org.swiftleap.common.security.Obs;

import javax.mail.*;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Created by ruans on 2017/11/27.
 */
public abstract class AbstractMailHandler<MessageType> implements MailHandler, Runnable {
    private static Log log = LogFactory.getLog(AbstractMailHandler.class);

    Thread thread = null;
    volatile boolean running = false;

    protected abstract Flags.Flag[] handle(MessageType mailMessage) throws HandlerException;

    /**
     * Prepare or ignore a message.
     *
     * @param message
     * @return null if this message should be ignored, exception on error.
     * @throws HandlerException
     */
    protected abstract MessageType prepareOrIgnore(Message message) throws HandlerException;

    @Override
    public synchronized void send(MailMessage simpleMessage) throws HandlerException {
        try {
            new SmtpUtil(getConf())
                    .sendMail(simpleMessage.getFrom()
                            , simpleMessage.getTo()
                            , simpleMessage.getCc()
                            , simpleMessage.getSubject()
                            , simpleMessage.getMainContent()
                            , simpleMessage.getAttachments());
        } catch (EmailException | MessagingException e) {
            throw new HandlerException(e);
        }
    }

    public synchronized void start() {
        if (isRunning())
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public final void run() {
        try {
            while (running) {
                try {
                    processInbox();
                } catch (Exception ex) {
                    log.error("Processing inbox failed in main loop", ex);
                }
                Thread.sleep(getConf().getProcessSeconds() * 1000);
            }
        } catch (Exception ex) {
            log.error("Processing failed in main loop", ex);
        } finally {
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized void stop() {
        running = false;
        if (thread != null)
            thread.interrupt();
        thread = null;
    }

    private Flags.Flag[] processMail(Message msg, MessageType messageType) throws Exception {

        log.info("Processing mail: " + msg.getSubject());

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        msg.writeTo(bout);

        return handle(messageType);
    }

    protected void processInbox() throws Exception {
        log.debug("Processing mails");
        val proto = getConf().getReceiveProtocol();
        if (proto == Conf.Protocol.POP)
            throw new Exception("Protocol not suported: " + proto.getValue());

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", proto.getValue());
        props.setProperty("mail.imap.starttls.enable", Boolean.toString(getConf().isReceiveTLS()));
        props.setProperty("mail.imap.ssl.checkserveridentity", "false");
        Store store = null;
        Folder inbox = null;
        try {
            Session session = Session.getInstance(props, null);
            store = session.getStore();
            store.connect(getConf().getReceiveHost(),
                    getConf().getReceivePort(),
                    getConf().getEmailUser(),
                    Obs.decryptIfObs(getConf().getEmailPassword()));

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            int count = inbox.getMessageCount();
            log.debug("Found x mails to process: " + count);
            if (count > 200) {
                log.error("Found " + count + " mails to process, this will take a long time to process, remember to clean the mail box often.");
            }
            for (int i = 1; i <= count; i++) {
                try {
                    Message msg = inbox.getMessage(i);

                    MessageType prepared = prepareOrIgnore(msg);
                    if (prepared == null) {
                        log.debug("Null prepared message, ignoring it.");
                        continue;
                    }

                    val flags = processMail(msg, prepared);
                    if (flags != null && flags.length > 0)
                        Stream.of(flags).forEach(flag -> {
                            try {
                                msg.setFlag(flag, true);
                            } catch (MessagingException ex) {
                                log.error("Failed to set flag: " + flag, ex);
                            }
                        });
                } catch (Exception ex) {
                    log.error("Failed to process message", ex);
                }
            }
        } finally {
            try {
                if (inbox != null)
                    inbox.close(true);
            } catch (Exception ex) {
                log.error("Failed to close inbox", ex);
            }
            try {
                if (store != null)
                    store.close();
            } catch (Exception ex) {
                log.error("Failed to close store", ex);
            }
        }
    }
}
