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

package com.cfitzarl.cfjwed.data.dto.converter;

import com.cfitzarl.cfjwed.data.dao.AccountDao;
import com.cfitzarl.cfjwed.data.dto.AccountDTO;
import com.cfitzarl.cfjwed.data.model.Account;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Container for bi-directional conversions between an {@link Account} and an {@link AccountDTO}.
 */
public class AccountConverterContainer {

    private AccountConverterContainer() { }

    @Component
    public static class AccountToDtoConverter implements Converter<Account, AccountDTO> {

        @Override
        public AccountDTO convert(MappingContext<Account, AccountDTO> mappingContext) {
            Account source = mappingContext.getSource();
            AccountDTO dto = new AccountDTO();
            dto.setId(source.getId());
            dto.setFirstName(source.getFirstName());
            dto.setLastName(source.getLastName());
            dto.setEmail(source.getEmail());

            return dto;
        }
    }

    @Component
    public static class DtoToAccountConverter implements Converter<AccountDTO, Account> {

        @Autowired
        private AccountDao accountDao;

        @Override
        public Account convert(MappingContext<AccountDTO, Account> mappingContext) {
            AccountDTO source = mappingContext.getSource();
            Account account = null;

            if (source.getId() != null) {
                account = accountDao.findOne(source.getId());
            }

            if (account == null) {
                account = new Account();
            }

            account.setFirstName(source.getFirstName());
            account.setLastName(source.getLastName());
            account.setEmail(source.getEmail());

            String pwd = account.getPassword();

            if ((source.getPassword() != null)
                    && ((account.getPassword() == null) || !BCrypt.checkpw(source.getPassword(), pwd))) {
                account.setPassword(BCrypt.hashpw(source.getPassword(), BCrypt.gensalt()));
            }

            if (source.getInvitationCode() != null) {
                account.setInvitationCode(source.getInvitationCode());
            }

            return account;
        }
    }
}
