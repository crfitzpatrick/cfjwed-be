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

package com.cfitzarl.cfjwed.data.model;

import com.cfitzarl.cfjwed.data.enums.AccountType;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.*;

@Data
@Entity
@Table(name = "accounts", indexes = {
        @Index(columnList = "id", name = "account_id_hindex"),
        @Index(columnList = "email", name = "accounnt_name_hindex")
})
public class Account extends AbstractIdBase {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private String email;

    @Column
    private String password;

    @Column(name = "account_type", updatable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type = AccountType.INVITEE;

    @Column
    private boolean activated;

    @Transient
    private String invitationCode;

    public void setPassword(String clearText) {
        password = BCrypt.hashpw(clearText, BCrypt.gensalt());
    }
}
