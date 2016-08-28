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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl testee = new AccountServiceImpl();

    private AccountDao accountDao = mock(AccountDao.class);

    private Account account = new Account();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindByAccountTypeReturnsCorrectAccount() {
        when(accountDao.findByType(AccountType.ADMIN)).thenReturn(Collections.singletonList(account));
        assertTrue(testee.find(AccountType.ADMIN).equals(Collections.singletonList(account)));
        assertEquals(testee.find(AccountType.INVITEE).size(), 0);
        verify(accountDao, times(2)).findByType(any(AccountType.class));
    }

    @Test
    public void testFindByIdReturnsCorrectAccount() {
        UUID foundId = UUID.randomUUID();
        when(accountDao.findOne(foundId)).thenReturn(account);
        assertTrue(testee.find(foundId).equals(account));
        assertNull(testee.find(UUID.randomUUID()));
        verify(accountDao, times(2)).findOne(any());
    }

    @Test
    public void findByEmailReturnsCorrectAccount() throws Exception {
        when(accountDao.findByEmail("email@email.com")).thenReturn(account);
        assertTrue(testee.findByEmail("email@email.com").equals(account));
        assertNull(testee.findByEmail("nope"));
        verify(accountDao, times(2)).findByEmail(anyString());
    }

    @Test
    public void hasValidCredsReturnsCorrectResponse() throws Exception {
        account.setPassword("password");
        assertTrue(testee.hasValidCreds(account, "password"));
        assertFalse(testee.hasValidCreds(account, "notPassword"));
    }

    @Test
    public void testSavePersistsAccount() throws Exception {
        when(accountDao.save(account)).thenReturn(account);
        assertEquals(testee.save(account), account);
        verify(accountDao, times(1)).save(account);
    }
}