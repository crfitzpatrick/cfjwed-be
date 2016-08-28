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

package com.cfitzarl.cfjwed.service;

import com.cfitzarl.cfjwed.data.model.Account;
import com.cfitzarl.cfjwed.data.model.Invitation;

import java.util.List;
import java.util.UUID;

/**
 * This handles all logic related to invitation management.
 */
public interface InvitationService {

    /**
     * This deletes an {@link Invitation}.
     *
     * @param id the id of the invitation
     */
    void delete(UUID id);

    /**
     * Returns an {@link Invitation} if one is found.
     *
     * @param id the id of the invitation
     * @return
     */
    Invitation find(UUID id);

    /**
     * Returns an invitation for a particular {@link Account}.
     *
     * @param account the account
     * @return the invitation
     */
    Invitation findByAccount(Account account);

    /**
     * This returns a list of all {@link Invitation}s.
     *
     * @return all invitations
     */
    List<Invitation> find();

    /**
     * This returns a list of all {@link Invitation}s where the invitee has not yet responded.
     * @return
     */
    List<Invitation> findUnresponded();

    /**
     * Returns whether or not an {@link Invitation} exists with the provided code.
     *
     * @param code the code to check
     * @return whether the code is already taken
     */
    boolean isUnusedCode(String code);

    /**
     * This will modify an existing {@link Invitation} or create a new one if it does not exist.
     *
     * @param invitation the invitation to modify or create
     */
    Invitation upsert(Invitation invitation);
}
