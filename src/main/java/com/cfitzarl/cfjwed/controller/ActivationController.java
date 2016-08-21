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

import com.cfitzarl.cfjwed.data.dto.AccountDTO;
import com.cfitzarl.cfjwed.data.dto.ActivateDTO;
import com.cfitzarl.cfjwed.data.enums.AccountType;
import com.cfitzarl.cfjwed.data.model.Account;
import com.cfitzarl.cfjwed.data.model.Activation;
import com.cfitzarl.cfjwed.exception.BadRequestException;
import com.cfitzarl.cfjwed.exception.ResourceNotFoundException;
import com.cfitzarl.cfjwed.service.RegistrationService;
import com.cfitzarl.cfjwed.service.AccountService;
import com.cfitzarl.cfjwed.service.ActivationService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * This contains all APIs referring to {@link Activation}s.
 */
@Controller
@ResponseBody
@RequestMapping("/api/activations")
public class ActivationController {

    @Autowired
    private ActivationService activationService;

    @Autowired
    private AccountService accountService;

    @Autowired
    @Qualifier("inviteeRegistrationService")
    private RegistrationService registrationService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * This creates an account and an associated activation record.
     *
     * @param accountDTO
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void createAccount(@RequestBody AccountDTO accountDTO) {
        Account account = modelMapper.map(accountDTO, Account.class);
        account.setType(AccountType.INVITEE);
        registrationService.register(account);
    }

    /**
     * This returns activation information based upon an activation's landing token.
     *
     * @param landingToken the token associated with the activation
     * @return
     */
    @RequestMapping(value = "/{landingToken}", method = RequestMethod.GET)
    public ActivateDTO providePasswordSetupData(@PathVariable String landingToken) {
        getActivation(landingToken);
        return new ActivateDTO();
    }

    /**
     * This will validate a password, mark the account as activate, and then save it.
     *
     * @param landingToken the token associated with the activation
     * @param activateDTO the activation DTO
     */
    @RequestMapping(value = "/{landingToken}", method = RequestMethod.PUT)
    public void activateAccount(@PathVariable String landingToken, @Valid @RequestBody ActivateDTO activateDTO) {
        if (StringUtils.isBlank(activateDTO.getPassword())) {
            throw new BadRequestException("Empty password provided");
        }

        if (!StringUtils.equals(activateDTO.getPassword(), activateDTO.getConfirmPassword())) {
            throw new BadRequestException("Password and its confirm password must match");
        }

        Activation activation = getActivation(landingToken);
        Account account = activation.getAccount();
        account.setPassword(activateDTO.getPassword());

        accountService.save(account);
        activationService.activate(landingToken);
    }

    /**
     * This returns an {@link Activation} by its token and throws a {@link ResourceNotFoundException} if
     * one is not found.
     *
     * @param landingToken the token
     * @return the activation
     */
    private Activation getActivation(String landingToken) {
        Activation activation = activationService.find(landingToken);

        if (activation == null) {
            throw new ResourceNotFoundException("No activation found with an id = %s", landingToken);
        }

        return activation;
    }
}
