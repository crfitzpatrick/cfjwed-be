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

package com.cfitzarl.cfjwed.controller;

import com.cfitzarl.cfjwed.core.security.SecurityContextWrapper;
import com.cfitzarl.cfjwed.core.security.SessionConstant;
import com.cfitzarl.cfjwed.data.dto.AccountDTO;
import com.cfitzarl.cfjwed.data.dto.AuthenticationDTO;
import com.cfitzarl.cfjwed.data.model.Account;
import com.cfitzarl.cfjwed.exception.UnauthorizedException;
import com.cfitzarl.cfjwed.service.AccountService;
import com.cfitzarl.cfjwed.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * This contains all APIs related to adjusting {@link Account} settings and data.
 */
@Controller
@ResponseBody
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RedisService redisService;

    /**
     * This returns the current account's information.
     *
     * @return an account DTO
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public AccountDTO displayAccountConfigPage() {
        return modelMapper.map(
            accountService.find(SecurityContextWrapper.getId()),
            AccountDTO.class
        );
    }

    /**
     * This updates the current account's information.
     *
     * @param accountDTO the account DTO
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public void updateAccount(@Valid @RequestBody AccountDTO accountDTO) {
        accountDTO.setId(SecurityContextWrapper.getId());
        accountService.save(modelMapper.map(accountDTO, Account.class));
    }

    /**
     * This API provides account-specific information to the UI after a page refresh.
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public AuthenticationDTO getAuthenticationDetails(HttpServletRequest request) throws Exception {
        String authToken = request.getHeader(SessionConstant.AUTH_TOKEN_HEADER);

        if (authToken != null) {
            String serializedAuthData = redisService.get(authToken);

            if (serializedAuthData != null) {
                return new ObjectMapper().readValue(serializedAuthData, AuthenticationDTO.class);
            }
        }

        throw new UnauthorizedException("Could not find token");
    }
}
