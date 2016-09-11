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

package com.cfitzarl.cfjwed.data.dao;

import com.cfitzarl.cfjwed.data.enums.ResponseStatus;
import com.cfitzarl.cfjwed.data.model.Attendant;
import com.cfitzarl.cfjwed.data.model.Invitation;
import com.cfitzarl.cfjwed.data.model.MealOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * The DAO representing the {@link Attendant} data model.
 */
@Repository
public interface AttendantDao extends JpaRepository<Attendant, UUID> {

    /**
     * Returns the number of attendants have chosen the specified {@link MealOption}.
     *
     * @param mealOption the meal option to filter by
     * @return the number of people who have chosen it
     */
    long countByMeal(MealOption mealOption);

    /**
     * Returns the number of attendants by their {@link ResponseStatus}.
     *
     * @param status the status to filter by
     * @return the count
     */
    long countByResponseStatus(ResponseStatus status);

    /**
     * Returns the attends registered against a particular invitation.
     *
     * @param invitation the invitation
     * @return a list of attendants if present
     */
    List<Attendant> findByInvitation(Invitation invitation);
}
