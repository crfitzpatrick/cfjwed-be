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

import com.cfitzarl.cjfwed.data.dao.ActivationDao;
import com.cfitzarl.cjfwed.data.model.Activation;
import com.cfitzarl.cjfwed.exception.BadRequestException;
import com.cfitzarl.cjfwed.service.AccountService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActivationServiceImplTest {

    @InjectMocks
    private ActivationServiceImpl testee = new ActivationServiceImpl();

    private AccountService accountService = mock(AccountService.class);

    private ActivationDao activationDao = spy(ActivationDao.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = BadRequestException.class)
    public void testActivateThrowsBadRequestExceptionWhenActivationNotFound() {
        when(activationDao.findByToken(anyString())).thenReturn(null);
        testee.activate("token");
        verify(activationDao, times(1)).findByToken(anyString());
    }

    @Test
    public void findActivationReturnsCorrectActivation() {
        when(activationDao.findByToken("correctToken")).thenReturn(new Activation());
        assertNotNull(testee.find("correctToken"));
        assertNull(testee.find("incorrectToken"));
        verify(activationDao, times(2)).findByToken(anyString());
    }

    @Test
    public void testSavePersistsActivation() {
        Activation activation = new Activation();
        testee.save(activation);
        verify(activationDao, times(1)).save(any(Activation.class));
    }
}