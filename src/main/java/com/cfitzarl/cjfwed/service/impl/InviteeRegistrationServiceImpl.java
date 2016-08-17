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
import com.cfitzarl.cjfwed.data.dao.InvitationDao;
import com.cfitzarl.cjfwed.data.enums.AccountType;
import com.cfitzarl.cjfwed.data.model.Account;
import com.cfitzarl.cjfwed.data.model.Invitation;
import com.cfitzarl.cjfwed.exception.BadRequestException;
import com.cfitzarl.cjfwed.service.AccountService;
import com.cfitzarl.cjfwed.service.EmailDispatchingService;
import com.cfitzarl.cjfwed.service.LocalizationService;
import com.cfitzarl.cjfwed.service.RegistrationService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service("inviteeRegistrationService")
public class InviteeRegistrationServiceImpl extends RegistrationService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private EmailDispatchingService emailDispatcher;

    @Autowired
    private InvitationDao invitationDao;

    @Autowired
    private LocalizationService localizationService;

    /** {@inheritDoc} **/
    @Override
    public void register(Account account) {
        Invitation invitation = invitationDao.findByCode(account.getInvitationCode());

        if (invitation == null) {
            throw new BadRequestException("Invalid invitation code provided");
        }

        if (invitation.getAccount() != null) {
            throw new BadRequestException("Invitation code already used");
        }

        // Associate account with invitation
        invitation.setAccount(accountService.save(account));
        invitationDao.save(invitation);

        String activationToken = RandomStringUtils.randomAlphanumeric(32);

        // Send registration email
        String title = configDao.findByKey("event.title").getValue();
        String subject = localizationService.getMessage("email.activation.invitee.subject", Collections.singletonList(title));
        Map<String, Object> attrs = getCommonEmailAttrs(activationToken, account);
        emailDispatcher.send(account.getEmail(), subject, "invitee-activation-email", attrs);

        // Create activation
        createActivation(activationToken, account);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean supports(Account account) {
        return (account != null) && (account.getType() != null) && (account.getType() == AccountType.INVITEE);
    }
}
