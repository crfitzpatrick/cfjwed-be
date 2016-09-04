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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.cfitzarl.cfjwed.service.LocalizationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocalizationServiceImpl implements LocalizationService {

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationServiceImpl.class);

    /** {@inheritDoc} **/
    @Override
    public boolean codeExists(String code) {
        return (code != null) && !code.equals(getMessage(code));
    }

    /** {@inheritDoc} **/
    @Override
    public String getMessage(String code) {
        return getMessage(code, new ArrayList<>());
    }

    /** {@inheritDoc} **/
    @Override
    public String getMessage(String code, List<?> args) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = code;

        try {
            message = messageSource.getMessage(code, (args == null) ? null : args.toArray(), locale);
        } catch (NoSuchMessageException e) {
            LOGGER.error(String.format("Could not find message with key %s", code));
        }

        return message;
    }
}
