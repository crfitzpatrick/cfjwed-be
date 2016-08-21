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

import com.cfitzarl.cfjwed.data.enums.ConfigKey;
import com.cfitzarl.cfjwed.service.LocalizationService;
import com.cfitzarl.cfjwed.data.dao.ConfigDao;
import com.cfitzarl.cfjwed.data.dto.ConfigDTO;
import com.cfitzarl.cfjwed.data.model.Config;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Container for bi-directional conversions between a {@link Config} and a {@link ConfigDTO}.
 */
public class ConfigConverterContainer {

    private ConfigConverterContainer() { }

    @Component
    public static class ConfigToDtoConverter implements Converter<Config, ConfigDTO> {

        @Autowired
        private LocalizationService localizationService;

        @Override
        public ConfigDTO convert(MappingContext<Config, ConfigDTO> mappingContext) {
            Config source = mappingContext.getSource();

            ConfigDTO dto = new ConfigDTO();
            dto.setDisplayName(localizationService.getMessage(source.getKey().getTranslationKey()));
            dto.setDisplayType(source.getDisplayType());
            dto.setKey(source.getKey().getTranslationKey());
            dto.setValue(source.getValue());

            return dto;
        }
    }

    @Component
    public static class DtoToConfigConverter implements Converter<ConfigDTO, Config> {

        @Autowired
        private ConfigDao configDao;

        @Override
        public Config convert(MappingContext<ConfigDTO, Config> mappingContext) {
            ConfigDTO source = mappingContext.getSource();
            ConfigKey key = ConfigKey.fromTranslationKey(source.getKey());
            Config config = configDao.findByKey(key);

            if (config != null) {
                if (!config.getValue().equals(source.getValue())) {
                    config.setValue(source.getValue());
                }
            }

            return config;
        }
    }
}
