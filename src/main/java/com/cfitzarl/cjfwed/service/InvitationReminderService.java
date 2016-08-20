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

package com.cfitzarl.cjfwed.service;

import com.cfitzarl.cjfwed.data.enums.ResponseStatus;
import com.cfitzarl.cjfwed.data.model.Attendant;
import com.cfitzarl.cjfwed.data.model.Invitation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

/**
 * This is responsible for sending reminder emails to invitees who have not yet responded.
 */
@Component
public class InvitationReminderService {

    @Autowired
    private InvitationService invitationService;

    @Value("${reminder.interval}")
    private int timeInterval = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationReminderService.class);

    @Transactional
    @Scheduled(cron = "0 0 15 1/1 * ?")
    public void sendReminderEmails() {
        LOGGER.info("Running email reminder job");

        for (Invitation invitation : invitationService.findUnresponded()) {
            List<Attendant> attendants = invitation.getAttendants();

            if (attendants.stream().filter(hasNotResponded()).count() > 0) {

            }
        }
    }

    private static Predicate<Attendant> hasNotResponded() {
        return attendant -> attendant.getResponseStatus() == ResponseStatus.PENDING;
    }
}
