/*
 * MIT License
 *
 * Copyright (c) 2016  Christopher R. Fitzpatrick
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cfitzarl.cfjwed.service;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * This is an abstraction layer over {@link JavaMailSenderImpl} and the {@link VelocityEngine} that constructs and sends
 * emails to a specified email address.
 */
public interface EmailDispatchingService {

    /**
     * Accepts an email address, a template name, and a {@link Map} of attributes and translates them into an email that
     * is queued up and pushed onto a {@link ThreadPoolTaskExecutor} using the {@link MailRunner}. The template name is
     * relative to the "email-templates" folder and excludes the type specification (text, html). The attributes map is
     * provided to the {@link VelocityEngine} in order to parse the content of the requested template.
     *
     * @param to the address in which the email is sent
     * @param template the name of the template
     * @param attrs the attributes provides to the template
     */
    void send(String to, String subject, String template, Map<String, Object> attrs);

    /**
     * Runnable that takes a {@link MimeMessage} and sends it out using the {@link JavaMailSender}.
     */
    class MailRunner implements Runnable {

        private MimeMessage mimeMessage;
        private JavaMailSender javaMailSender;

        public MailRunner(MimeMessage mimeMessage, JavaMailSender sender) {
            this.mimeMessage = mimeMessage;
            this.javaMailSender = sender;
        }

        @Override
        public void run() {
            javaMailSender.send(mimeMessage);
        }
    }
}
