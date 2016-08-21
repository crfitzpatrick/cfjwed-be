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

import com.cfitzarl.cfjwed.data.dto.MealOptionDTO;
import com.cfitzarl.cfjwed.data.model.MealOption;
import com.cfitzarl.cfjwed.service.MealOptionService;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Container for bi-directional conversions between a {@link MealOption} and a {@link MealOptionDTO}.
 */
public class MealOptionConverterContainer {

    private MealOptionConverterContainer() { }

    @Component
    public static class DtoToMealOptionConverter implements Converter<MealOptionDTO, MealOption> {

        @Autowired
        private MealOptionService mealOptionService;

        @Override
        public MealOption convert(MappingContext<MealOptionDTO, MealOption> mappingContext) {
            MealOptionDTO source = mappingContext.getSource();
            MealOption option = new MealOption();

            if (source.getId() != null) {
                option = mealOptionService.find(source.getId());
            }

            option.setName(source.getName());
            option.setDescription(source.getDescription());

            return option;
        }
    }

    @Component
    public static class MealOptionToDtoConverter implements Converter<MealOption, MealOptionDTO> {

        @Override
        public MealOptionDTO convert(MappingContext<MealOption, MealOptionDTO> mappingContext) {
            MealOption source = mappingContext.getSource();

            if (source == null) { return null; }

            MealOptionDTO dto = new MealOptionDTO();
            dto.setId(source.getId());
            dto.setName(source.getName());
            dto.setDescription(source.getDescription());

            return dto;
        }
    }
}
