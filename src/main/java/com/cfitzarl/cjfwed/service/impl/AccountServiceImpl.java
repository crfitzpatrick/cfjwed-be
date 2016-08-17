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

import com.cfitzarl.cjfwed.data.dao.AccountDao;
import com.cfitzarl.cjfwed.data.enums.AccountType;
import com.cfitzarl.cjfwed.data.model.Account;
import com.cfitzarl.cjfwed.exception.DuplicateEmailException;
import com.cfitzarl.cjfwed.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    protected AccountDao accountDao;

    /** {@inheritDoc} **/
    @Override
    public void delete(Account account) {
        accountDao.delete(account);
    }

    /** {@inheritDoc} **/
    @Override
    public List<Account> find(AccountType accountType) {
        return accountDao.findByType(accountType);
    }

    /** {@inheritDoc} **/
    @Override
    public Account find(Long id) {
        return accountDao.findOne(id);
    }

    /** {@inheritDoc} **/
    @Override
    public Account findByEmail(String email) {
        return accountDao.findByEmail(email);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean hasValidCreds(Account account, String creds) {
        return BCrypt.checkpw(creds, account.getPassword());
    }

    /** {@inheritDoc} **/
    @Override
    public Account save(Account account) {
        Account existing = accountDao.findByEmail(account.getEmail());

        // Handle email duplication
        if ((existing != null) && !existing.getId().equals(account.getId())) {
            throw new DuplicateEmailException();
        }

        return accountDao.save(account);
    }
}
