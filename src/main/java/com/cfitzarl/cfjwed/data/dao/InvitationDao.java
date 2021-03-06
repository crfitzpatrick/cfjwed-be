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

import com.cfitzarl.cfjwed.data.model.Account;
import com.cfitzarl.cfjwed.data.model.Invitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * The DAO representing the {@link Invitation} data model.
 */
@Repository
public interface InvitationDao extends JpaRepository<Invitation, UUID> {

    /**
     * Returns the invitation associated with a particular account.
     *
     * @param account the account
     * @return the invitation
     */
    Invitation findByAccount(Account account);

    /**
     * Returns a paginated slice of {@link Invitation}s.
     *
     * @param pageable the page data
     * @return a paginated slice
     */
    Page<Invitation> findAll(Pageable pageable);

    /**
     * Returns an invitation by its registration code.
     *
     * @param code the registration code
     * @return the invitation if it exists
     */
    Invitation findByCode(String code);

    /**
     * Returns a list of invitations in ascending order by name.
     *
     * @return the invitations
     */
    List<Invitation> findAllByOrderByNameAsc();

    /**
     * Returns whether an invitation exists with the given code.
     *
     * @param code the code to check
     * @return whether the code is taken
     */
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Invitation i WHERE i.code = :code")
    boolean isCodeTaken(@Param("code") String code);
}
