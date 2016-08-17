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

import com.cfitzarl.cjfwed.data.enums.AccountType;
import com.cfitzarl.cjfwed.data.model.Account;

import java.util.List;

/**
 * This class defines and provides functionality for account manipulation.
 */
public interface AccountService {

    /**
     * Deletes an account.
     *
     * @param account the account to delete
     */
    void delete(Account account);

    /**
     * Returns a list of accounts by their account type.
     *
     * @param accountType the account type
     * @return a list of accounts
     */
    List<Account> find(AccountType accountType);

    /**
     * Returns an account based on its primary key.
     *
     * @param id the ID of the requested account
     * @return the account
     */
    Account find(Long id);

    /**
     * Returns an account based on its email.
     *
     * @param email the email of the account
     * @return the account
     */
    Account findByEmail(String email);

    /**
     * Returns whether or not the has produced from the provided creds match that of the provided account.
     *
     * @param account the account to check
     * @param creds the creds to match against
     * @return whether the creds are valid
     */
    boolean hasValidCreds(Account account, String creds);

    /**
     * Saves an account.
     *
     * @param account the account to save
     */
    Account save(Account account);
}
