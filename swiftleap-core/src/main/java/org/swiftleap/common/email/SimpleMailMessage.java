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

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ruan on 2015/06/04.
 */
@Getter
@Setter
public class SimpleMailMessage implements MailMessage {
    private String from;
    private List<String> to;
    private List<String> cc = new ArrayList<>(0);
    private String subject;
    private String mainContent;
    private List<Attachment> attachments = new ArrayList<>(0);
    private Flags flags;
    private Date dateTime;

    public SimpleMailMessage() {
    }

    public SimpleMailMessage(MailMessage message) {
        from = message.getFrom();
        to = message.getTo();
        cc = message.getCc();
        subject = message.getSubject();
        mainContent = message.getMainContent();
        attachments = message.getAttachments();
        flags = message.getFlags();
        dateTime = message.getDateTime();
    }

    public SimpleMailMessage(Message msg) throws Exception {
        try {
            val dateTime = msg.getSentDate();
            setDateTime(dateTime == null ? new Date() : dateTime);
        } catch (Exception ex) {
            setDateTime(new Date());
        }
        setSubject(msg.getSubject());
        Address[] addrs = msg.getFrom();
        setFrom(addrs[0].toString());
        Address[] ccs = msg.getRecipients(Message.RecipientType.CC);
        if (ccs != null) {
            List<String> ccList = new ArrayList<>();
            for (Address a : ccs) {
                ccList.add(a.toString());
            }
            setCc(ccList);
        }

        setFlags(msg.getFlags());

        Object content = msg.getContent();
        if (content instanceof String) {
            setMainContent(content.toString());
        } else if (msg instanceof MimeMessage) {
            MimeMessageParser parser = new MimeMessageParser((MimeMessage) msg);
            parser = parser.parse();
            String mainContent = parser.getPlainContent();
            if (mainContent == null || mainContent.isEmpty()) {
                //mainContent = parser.getHtmlContent();
                //mime = "text/html";
                //TODO strip bad stuff
                throw new Exception("Html messages are not supported yet");
            }
            setMainContent(mainContent);
        } else {
            throw new Exception("Unknown content: " + content);
        }
    }

    public void setCc(List<String> cc) {
        if (cc == null) {
            this.cc = new ArrayList<>(0);
            return;
        }
        this.cc = cc;
    }


    public void setSubject(String subject) {
        if (subject == null) {
            this.subject = "";
            return;
        }
        this.subject = subject;
    }

    public void setMainContent(String mainContent) {
        if (mainContent == null) {
            this.mainContent = "";
            return;
        }
        this.mainContent = mainContent;
    }

    public void setTo(String to) {
        val newTo = new ArrayList<String>(0);
        newTo.add(to);
        this.to = newTo;
    }

}
