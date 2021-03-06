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

import com.cfitzarl.cfjwed.data.dao.AttendantDao;
import com.cfitzarl.cfjwed.data.dao.InvitationDao;
import com.cfitzarl.cfjwed.data.enums.ResponseStatus;
import com.cfitzarl.cfjwed.data.model.Attendant;
import com.cfitzarl.cfjwed.data.model.Invitation;
import com.cfitzarl.cfjwed.exception.BadRequestException;
import com.cfitzarl.cfjwed.service.AttendantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AttendantServiceImpl implements AttendantService {

    @Autowired
    private AttendantDao attendantDao;

    @Autowired
    private InvitationDao invitationDao;

    /** {@inheritDoc} **/
    @Override
    public long countByStatus(ResponseStatus status) {
        return attendantDao.countByResponseStatus(status);
    }

    /** {@inheritDoc} **/
    @Override
    public void delete(UUID id) {
        attendantDao.delete(id);
    }

    /** {@inheritDoc} **/
    @Override
    public List<Attendant> find() {
        return attendantDao.findAll();
    }

    /** {@inheritDoc} **/
    @Override
    public List<Attendant> findByInvitation(UUID id) {
        Invitation invitation = invitationDao.findOne(id);

        if (invitation == null) {
            throw new BadRequestException("Invalid invitation ID provided");
        }

        return attendantDao.findByInvitation(invitation);
    }

    /** {@inheritDoc} **/
    @Override
    public Attendant find(UUID id) {
        return attendantDao.findOne(id);
    }

    /** {@inheritDoc} **/
    @Override
    public void save(Attendant attendant) {
        attendantDao.save(attendant);
    }
}
