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

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.swiftleap.common.email.Attachment;
import org.swiftleap.common.email.Conf;
import org.swiftleap.common.security.Obs;
import org.swiftleap.common.util.StringUtil;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ruan
 */
public class SmtpUtil {

    Conf conf;

    public SmtpUtil(Conf conf) {
        this.conf = conf;
    }

    private void sendMailPrv(String from,
                             Collection<String> to,
                             Collection<String> cc,
                             String subject,
                             String htmlMessage,
                             List<MimeMultipart> multiParts) throws EmailException {

        String host = conf.getSmtpHost();
        int port = conf.getSmtpPort();
        String user = conf.getEmailUser();
        String password = Obs.decryptIfObs(conf.getEmailPassword());
        if (StringUtil.isNullOrWhites(from)) {
            from = conf.getEmailAddress();
        }
        boolean tls = conf.isSmtpTLS();

        HtmlEmail email = new HtmlEmail();
        email.setHostName(host);
        email.setSmtpPort(port);
        if (user != null && user.length() > 0 && password != null) {
            email.setAuthenticator(new DefaultAuthenticator(user, password));
        }
        email.setStartTLSEnabled(tls);
        email.setFrom(from);
        email.setSubject(subject);
        email.setHtmlMsg(htmlMessage);
        if (multiParts != null) {
            for (MimeMultipart m : multiParts) {
                email.addPart(m);
            }
        }
        for (String t : to) {
            email.addTo(t);
        }
        if (cc != null) {
            for (String c : cc) {
                email.addCc(c);
            }
        }
        email.send();

    }

    public void sendMail(Collection<String> to,
                         Collection<String> cc,
                         String subject,
                         String htmlMessage,
                         List<MimeMultipart> multiParts) throws EmailException {

        sendMailPrv(null, to, cc, subject, htmlMessage, multiParts);
    }

    public void sendMail(String to, String subject, String htmlMessage, Attachment attachment) throws EmailException, MessagingException {
        List<String> toList = new ArrayList<>();
        List<String> ccList = new ArrayList<>();
        List<MimeMultipart> parts = new ArrayList<>();

        MimeBodyPart bp = new MimeBodyPart();
        bp.setDataHandler(new DataHandler(new ByteArrayDataSource(attachment.getData(), attachment.getMime())));
        bp.setFileName(attachment.getFileName());

        MimeMultipart mp = new MimeMultipart();
        mp.addBodyPart(bp);

        toList.add(to);

        parts.add(mp);

        sendMailPrv(null, toList, ccList, subject, htmlMessage, parts);
    }

    public void sendMail(String from, List<String> to, List<String> cc, String subject, String htmlMessage, List<Attachment> attachments) throws EmailException, MessagingException {
        List<String> toList = to;
        List<String> ccList = cc;
        List<MimeMultipart> parts = new ArrayList<>();

        for (Attachment attachment : attachments) {
            MimeMultipart mp = new MimeMultipart();
            MimeBodyPart bp = new MimeBodyPart();
            bp.setDataHandler(new DataHandler(new ByteArrayDataSource(attachment.getData(), attachment.getMime())));
            bp.setFileName(attachment.getFileName());
            mp.addBodyPart(bp);
            parts.add(mp);
        }

        sendMailPrv(from, toList, ccList, subject, htmlMessage, parts);
    }

    public void sendMail(String to, String subject, String htmlMessage) throws EmailException {
        List<String> toList = new ArrayList<>();
        List<String> ccList = new ArrayList<>();
        List<MimeMultipart> parts = new ArrayList<>();

        toList.add(to);

        sendMailPrv(null, toList, ccList, subject, htmlMessage, parts);
    }
}
