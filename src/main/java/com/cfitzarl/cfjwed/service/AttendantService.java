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

import com.cfitzarl.cfjwed.data.model.Attendant;

import java.util.List;

/**
 * This provides functionality relating to attendants.
 */
public interface AttendantService {

    /**
     * This deletes an attendant by its primary key.
     *
     * @param id the id
     */
    void delete(Long id);

    /**
     * This returns all attendants.
     *
     * @return a list of all attendants
     */
    List<Attendant> find();

    /**
     * This returns a list of all attendants belonging to a specific invitation.
     *
     * @param id the primary key of the invitation
     * @return a list of attendants
     */
    List<Attendant> findByInvitation(Long id);

    /**
     * Find an attendant by its primary key.
     *
     * @param id the id
     * @return the attendant if it exists
     */
    Attendant find(Long id);

    /**
     * This saves an attendant.
     *
     * @param attendant the attendant to save
     */
    void save(Attendant attendant);
}
