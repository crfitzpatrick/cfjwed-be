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

package com.cfitzarl.cjfwed.service.impl;

import com.cfitzarl.cjfwed.data.dao.ConfigDao;
import com.cfitzarl.cjfwed.service.EmailDispatchingService;
import com.cfitzarl.cjfwed.service.LocalizationService;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailDispatchingServiceImpl implements EmailDispatchingService {

    @Value("${email.host}")
    private String host;

    @Value("${email.port}")
    private int port;

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private LocalizationService localizationService;

    @Autowired
    @Qualifier("emailTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private VelocityEngine velocityEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailDispatchingServiceImpl.class);

    /**
     * This is invoked after the DI container initializes the class. It is used to set the host and port that the
     * {@link JavaMailSenderImpl} uses to send the email. The default is localhost on port 25.
     */
    @PostConstruct
    private void configureMailSender() {
        javaMailSender.setHost(host);
        javaMailSender.setPort(Integer.valueOf(port));
    }

    /** {@inheritDoc} **/
    @Override
    public void send(String to, String subject, String template, Map<String, Object> attrs) {
        // Add message source decorator to attributes list for localization
        attrs.put("localeSource", localizationService);

        String htmlFilename = String.format("/email-templates/%s-html.vm", template);
        String textFilename = String.format("/email-templates/%s-text.vm", template);

        String htmlContent = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, htmlFilename, "UTF-8", attrs);
        String textContent = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, textFilename, "UTF-8", attrs);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String fromEmail = configDao.findByKey("event.email").getValue();
            String fromTitle = configDao.findByKey("event.title").getValue();

            String from = String.format("%s <%s>", fromTitle, fromEmail);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setReplyTo(from);
            helper.setText(textContent, htmlContent);

            taskExecutor.submit(new MailRunner(message, javaMailSender));
        } catch (MessagingException e) {
            LOGGER.error("Error generating mail message", e);
        }
    }
}
