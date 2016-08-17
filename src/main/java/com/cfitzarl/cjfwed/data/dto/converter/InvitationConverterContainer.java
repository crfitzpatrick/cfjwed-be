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

package com.cfitzarl.cjfwed.data.dto.converter;

import com.cfitzarl.cjfwed.data.dao.InvitationDao;
import com.cfitzarl.cjfwed.data.dto.InvitationDTO;
import com.cfitzarl.cjfwed.data.model.Invitation;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Container for bi-directional conversions between an {@link Invitation} and an {@link InvitationDTO}.
 */
public class InvitationConverterContainer {

    private InvitationConverterContainer() { }

    @Component
    public static class DtoToInvitationConverter implements Converter<InvitationDTO, Invitation> {

        @Autowired
        private InvitationDao invitationDao;

        @Override
        public Invitation convert(MappingContext<InvitationDTO, Invitation> mappingContext) {
            InvitationDTO source = mappingContext.getSource();
            Invitation invitation = new Invitation();

            if (source.getId() != null && invitationDao.exists(source.getId())) {
                invitation = invitationDao.findOne(source.getId());
            }

            if (source.getName() != null) {
                invitation.setName(source.getName());
            }

            if (source.getAddress() != null) {
                invitation.setAddress(source.getAddress());
            }

            return invitation;
        }
    }

    @Component
    public static class InvitationToDtoConverter implements Converter<Invitation, InvitationDTO> {

        @Autowired
        private ModelMapper _modelMapper;

        @Override
        public InvitationDTO convert(MappingContext<Invitation, InvitationDTO> mappingContext) {
            Invitation source = mappingContext.getSource();

            InvitationDTO invitationDTO = new InvitationDTO();
            invitationDTO.setId(source.getId());
            invitationDTO.setName(source.getName());
            invitationDTO.setAddress(source.getAddress());
            invitationDTO.setGuestCount(source.getAttendants().size());
            invitationDTO.setCode(source.getCode());

            return invitationDTO;
        }
    }
}
