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

import com.cfitzarl.cfjwed.data.dao.AccountDao;
import com.cfitzarl.cfjwed.data.enums.AccountType;
import com.cfitzarl.cfjwed.data.model.Account;
import com.cfitzarl.cfjwed.exception.DuplicateEmailException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account = new Account();

    @Test
    public void testDeleteDeletesAccount() {
        accountService.delete(account);

        verify(accountDao, times(1)).delete(account);
    }

    @Test
    public void testFindByAccountTypeReturnsCorrectAccount() {
        when(accountDao.findByType(AccountType.ADMIN)).thenReturn(Collections.singletonList(account));

        assertTrue(accountService.find(AccountType.ADMIN).equals(Collections.singletonList(account)));
        assertEquals(accountService.find(AccountType.INVITEE).size(), 0);

        verify(accountDao, times(2)).findByType(any(AccountType.class));
    }

    @Test
    public void testFindReturnsCorrectAccount() {
        UUID foundId = UUID.randomUUID();
        when(accountDao.findOne(foundId)).thenReturn(account);

        assertTrue(accountService.find(foundId).equals(account));
        assertNull(accountService.find(UUID.randomUUID()));

        verify(accountDao, times(2)).findOne(any());
    }

    @Test
    public void findByEmailReturnsCorrectAccount() throws Exception {
        when(accountDao.findByEmail("email@email.com")).thenReturn(account);

        assertTrue(accountService.findByEmail("email@email.com").equals(account));
        assertNull(accountService.findByEmail("nope"));

        verify(accountDao, times(2)).findByEmail(anyString());
    }

    @Test
    public void hasValidCredsReturnsCorrectResponse() throws Exception {
        account.setPassword("password");

        assertTrue(accountService.hasValidCreds(account, "password"));
        assertFalse(accountService.hasValidCreds(account, "notPassword"));
    }

    @Test
    public void testSavePersistsAccount() throws Exception {
        when(accountDao.save(account)).thenReturn(account);
        assertEquals(accountService.save(account), account);
        verify(accountDao, times(1)).save(account);
    }

    @Test(expected = DuplicateEmailException.class)
    public void testSaveDuplicateEmailThrowsDuplicateEmailException() {
        String email = "test@email.com";
        account.setId(UUID.randomUUID());
        account.setEmail(email);

        when(accountDao.findByEmail(anyString())).thenReturn(account);

        Account dupedAccount = new Account();
        dupedAccount.setEmail(email);

        accountService.save(dupedAccount);
    }
}