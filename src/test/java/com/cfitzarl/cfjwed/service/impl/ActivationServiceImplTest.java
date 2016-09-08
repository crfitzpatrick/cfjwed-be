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
import com.cfitzarl.cfjwed.exception.ResourceNotFoundException;
import com.cfitzarl.cfjwed.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivationServiceImplTest {

    @InjectMocks
    private ActivationServiceImpl activationService;

    @Mock
    private AccountService accountService;

    @Mock
    private ActivationDao activationDao;

    @Test(expected = ResourceNotFoundException.class)
    public void testActivateThrowsResourceNotFoundWhenActivationNotFound() {
        when(activationDao.findByToken(anyString())).thenReturn(null);

        activationService.activate("token", "password");
    }

    @Test
    public void testActivateActivatesAndDeletesActivation() {
        Account account = new Account();
        Activation activation = new Activation();
        activation.setAccount(account);

        when(activationDao.findByToken("1234")).thenReturn(activation);

        activationService.activate("1234", "password");

        verify(activationDao, times(1)).delete(activation);
    }

    @Test
    public void testFindReturnsCorrectActivation() {
        when(activationDao.findByToken("correctToken")).thenReturn(new Activation());

        assertNotNull(activationService.find("correctToken"));
        assertNull(activationService.find("incorrectToken"));

        verify(activationDao, times(2)).findByToken(anyString());
    }

    @Test
    public void testSavePersistsActivation() {
        Activation activation = new Activation();
        activationService.save(activation);

        verify(activationDao, times(1)).save(any(Activation.class));
    }
}