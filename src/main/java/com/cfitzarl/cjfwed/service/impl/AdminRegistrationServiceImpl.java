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

import com.cfitzarl.cjfwed.data.enums.AccountType;
import com.cfitzarl.cjfwed.data.model.Account;
import com.cfitzarl.cjfwed.service.RegistrationService;
import com.cfitzarl.cjfwed.service.AccountService;
import com.cfitzarl.cjfwed.service.ActivationService;
import com.cfitzarl.cjfwed.service.EmailDispatchingService;
import com.cfitzarl.cjfwed.service.LocalizationService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("adminRegistrationService")
public class AdminRegistrationServiceImpl extends RegistrationService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ActivationService activationService;

    @Autowired
    private EmailDispatchingService emailDispatcher;

    @Autowired
    private LocalizationService localizationService;

    /** {@inheritDoc} **/
    @Override
    public void register(Account account) {
        String activationToken = RandomStringUtils.randomAlphanumeric(32);

        // Send registration email
        String subject = localizationService.getMessage("email.activation.admin.subject");
        Map<String, Object> attrs = getCommonEmailAttrs(activationToken, account);
        emailDispatcher.send(account.getEmail(), subject, "admin-activation", attrs);

        // Save the account
        accountService.save(account);

        // Create activation for account
        createActivation(activationToken, account);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean supports(Account account) {
        return (account != null) && (account.getType() != null) && (account.getType() == AccountType.ADMIN);
    }
}
