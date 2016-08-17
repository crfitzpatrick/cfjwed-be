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

package com.cfitzarl.cjfwed.controller;

import com.cfitzarl.cjfwed.data.dao.ConfigDao;
import com.cfitzarl.cjfwed.data.dto.ConfigDTO;
import com.cfitzarl.cjfwed.data.dto.ValidList;
import com.cfitzarl.cjfwed.data.model.Config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * This contains all APIs related to {@link Config}s.
 */
@Controller
@ResponseBody
@RequestMapping("/api/configs")
public class ConfigurationController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ConfigDao configDao;

    /**
     * This returns a list of configuration data in the form of a list of {@link ConfigDTO}s. These data are
     * pre-populated with the app.
     *
     * @return a list of configuration entries
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<ConfigDTO> provideSettings() {
        List<ConfigDTO> configs = new ArrayList<>();

        for (Config config : configDao.findAll()) {
            configs.add(modelMapper.map(config, ConfigDTO.class));
        }

        return configs;
    }

    /**
     * This loops through a list of {@link ConfigDTO}s, converts them to {@link Config}s, and saves them.
     *
     * @param configs the list of config DTOs
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void processConfig(@Valid @RequestBody ValidList<ConfigDTO> configs) {
        for (ConfigDTO configDto : configs.getList()) {
            configDao.save(modelMapper.map(configDto, Config.class));
        }
    }
}
