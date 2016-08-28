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
import com.cfitzarl.cfjwed.data.dao.InvitationDao;
import com.cfitzarl.cfjwed.data.model.Account;
import com.cfitzarl.cfjwed.data.model.Invitation;
import com.cfitzarl.cfjwed.service.InvitationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private InvitationDao invitationDao;

    /** {@inheritDoc} **/
    @Override
    public void delete(UUID id) {
        Invitation invitation = invitationDao.findOne(id);
        if (invitation != null) {
            if (invitation.getAccount() != null) {
                accountDao.delete(invitation.getAccount());
            }
            invitationDao.delete(id);
        }
    }

    /** {@inheritDoc} **/
    @Override
    public Invitation find(UUID id) {
        return invitationDao.findOne(id);
    }

    /** {@inheritDoc} **/
    @Override
    public Invitation findByAccount(Account account) {
        return invitationDao.findByAccount(account);
    }

    /** {@inheritDoc} **/
    @Override
    public List<Invitation> find() {
        return invitationDao.findAllByOrderByNameAsc();
    }

    @Override
    public List<Invitation> findUnresponded() {
        return null;
    }

    /** {@inheritDoc} **/
    @Override
    public Invitation upsert(Invitation invitation) {

        if ((invitation.getId() == null) || !invitationDao.exists(invitation.getId())) {
            String code;

            do {
                code = RandomStringUtils.randomAlphanumeric(6).toLowerCase();
            } while (invitationDao.isCodeTaken(code));

            invitation.setCode(code);
        }

        return invitationDao.save(invitation);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean isUnusedCode(String code) {
        Invitation invitation = invitationDao.findByCode(code);
        return (invitation != null) && (invitation.getAccount() != null);
    }
}
