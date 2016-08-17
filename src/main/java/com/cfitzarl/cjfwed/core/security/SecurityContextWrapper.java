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

package com.cfitzarl.cjfwed.core.security;

import com.cfitzarl.cjfwed.data.model.Account;
import com.cfitzarl.cjfwed.exception.UnauthorizedException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextWrapper {

    private SecurityContextWrapper() { }

    public static Long getId() {
        return (Long) getAuthentication().getPrincipal();
    }

    public static void authorize(Account account) {
        Object principal = getAuthentication().getPrincipal();
        if ((account == null || !account.getId().equals(principal)) && !"ROLE_ADMIN".equals(getRole())) {
            throw new UnauthorizedException("Unauthorized access detected");
        }
    }

    private static String getRole() {
        return getAuthentication().getAuthorities().iterator().next().getAuthority();
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) { throw new UnauthorizedException("Unauthorized access detected"); }
        return authentication;
    }
}
