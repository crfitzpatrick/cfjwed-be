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

package com.cfitzarl.cfjwed.service.impl;

import com.cfitzarl.cfjwed.data.dao.ActivationDao;
import com.cfitzarl.cfjwed.data.model.Account;
import com.cfitzarl.cfjwed.data.model.Activation;
import com.cfitzarl.cfjwed.exception.BadRequestException;
import com.cfitzarl.cfjwed.service.AccountService;
import com.cfitzarl.cfjwed.service.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivationServiceImpl implements ActivationService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ActivationDao activationDao;

    /** {@inheritDoc} **/
    @Override
    public void activate(String token) {
        Activation activation = activationDao.findByToken(token);

        if (activation == null) { throw new BadRequestException("Activation not found"); }

        Account account = activation.getAccount();
        account.setActivated(true);
        accountService.save(account);
    }

    /** {@inheritDoc} **/
    @Override
    public Activation find(String token) {
        return activationDao.findByToken(token);
    }

    /** {@inheritDoc} **/
    @Override
    public void save(Activation activation) {
        activationDao.save(activation);
    }
}
