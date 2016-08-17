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

package com.cfitzarl.cjfwed.controller;

import com.cfitzarl.cjfwed.core.security.SecurityContextWrapper;
import com.cfitzarl.cjfwed.data.dto.AccountDTO;
import com.cfitzarl.cjfwed.data.enums.AccountType;
import com.cfitzarl.cjfwed.data.model.Account;
import com.cfitzarl.cjfwed.exception.BadRequestException;
import com.cfitzarl.cjfwed.exception.ResourceNotFoundException;
import com.cfitzarl.cjfwed.service.RegistrationService;
import com.cfitzarl.cjfwed.service.AccountService;
import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * This contains all admin {@link Account}-specific APIs. All of these APIs require an admin role.
 */
@Controller
@ResponseBody
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("adminRegistrationService")
    private RegistrationService adminRegistrationService;

    /**
     * This returns a list of all admins.
     *
     * @return a list of admin account data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @JsonView(AccountDTO.View.Summary.class)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<AccountDTO> displayAllAdmins() {
        List<AccountDTO> accounts = new ArrayList<>();

        for (Account account : accountService.find(AccountType.ADMIN)) {
            accounts.add(modelMapper.map(account, AccountDTO.class));
        }

        return accounts;
    }

    /**
     * This will display an individual admin's account details.
     *
     * @param id the ID of the admin
     * @return the admin's account data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AccountDTO displayAdmin(@PathVariable Long id) {
        Account account = accountService.find(id);

        if (account == null) {
            throw new ResourceNotFoundException("Admin not found with id %s", id);
        }

        return modelMapper.map(account, AccountDTO.class);
    }

    /**
     * This deletes an admin's account.
     *
     * @param id the ID of the account to delete
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAdmin(@PathVariable Long id) {
        Account account = accountService.find(id);

        if (account == null) {
            throw new ResourceNotFoundException("No admin exists with id %s", id);
        }

        if (SecurityContextWrapper.getId().equals(id)) {
            throw new BadRequestException("Self-deletion is prohibited");
        }

        accountService.delete(account);
    }

    /**
     * This performs an upsert operation on the data provided.
     *
     * @param admin the admin's account data
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void upsertAdmin(@Valid @RequestBody AccountDTO admin) {
        Account account = modelMapper.map(admin, Account.class);
        account.setType(AccountType.ADMIN);
        adminRegistrationService.register(account);
    }
}
