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

import com.cfitzarl.cfjwed.data.dto.AttendantDTO;
import com.cfitzarl.cfjwed.data.enums.ResponseStatus;
import com.cfitzarl.cfjwed.data.model.Attendant;
import com.cfitzarl.cfjwed.data.model.Invitation;
import com.cfitzarl.cfjwed.data.model.MealOption;
import com.cfitzarl.cfjwed.exception.BadRequestException;
import com.cfitzarl.cfjwed.service.AttendantService;
import com.cfitzarl.cfjwed.service.InvitationService;
import com.cfitzarl.cfjwed.service.MealOptionService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Container for bi-directional conversions between an {@link Attendant} and an {@link AttendantDTO}.
 */
public class AttendantConverterContainer {

    private AttendantConverterContainer() { }

    @Component
    public static class AttendantToDtoConverter implements Converter<Attendant, AttendantDTO> {

        @Override
        public AttendantDTO convert(MappingContext<Attendant, AttendantDTO> mappingContext) {
            Attendant source = mappingContext.getSource();

            AttendantDTO dto = new AttendantDTO();
            dto.setId(source.getId());
            dto.setName(source.getName());
            dto.setResponseStatus(source.getResponseStatus());
            dto.setInvitation(source.getInvitation().getId());

            if (source.getMeal() != null) {
                dto.setDiningOption(source.getMeal().getId());
            }

            return dto;
        }
    }

    @Component
    public static class DtoToAttendantConverter implements Converter<AttendantDTO, Attendant> {

        @Autowired
        private InvitationService invitationService;

        @Autowired
        private AttendantService attendantService;

        @Autowired
        private MealOptionService mealOptionService;

        @Autowired
        private ModelMapper modelMapper;

        @Override
        public Attendant convert(MappingContext<AttendantDTO, Attendant> context) {
            AttendantDTO source = context.getSource();
            Attendant attendant = new Attendant();

            if ((source.getId() != null) && (attendantService.find(source.getId()) != null)) {
                attendant = attendantService.find(source.getId());
            } else {
                attendant.setId(source.getId());
            }

            attendant.setName(source.getName());

            ResponseStatus responseStatus = source.getResponseStatus();
            attendant.setResponseStatus((responseStatus == null) ? ResponseStatus.PENDING : responseStatus);

            MealOption option = null;

            if (source.getDiningOption() != null) {
                option = mealOptionService.find(source.getDiningOption());
            }

            attendant.setMeal(option);

            if (source.getInvitation() == null) {
                throw new BadRequestException("An attendant must have an invitation");
            }

            Invitation invitation = invitationService.find(source.getInvitation());
            attendant.setInvitation(invitation);

            return attendant;
        }
    }
}
