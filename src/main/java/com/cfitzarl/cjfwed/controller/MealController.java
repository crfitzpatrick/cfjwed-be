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

import com.cfitzarl.cjfwed.data.dto.MealOptionDTO;
import com.cfitzarl.cjfwed.data.model.MealOption;
import com.cfitzarl.cjfwed.service.MealOptionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/meals")
public class MealController {

    @Autowired
    private MealOptionService mealOptionService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<MealOptionDTO> getAllDiningOptions() {
        List<MealOptionDTO> dtoList = new ArrayList<>();

        for (MealOption options : mealOptionService.find()) {
            dtoList.add(modelMapper.map(options, MealOptionDTO.class));
        }

        return dtoList;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void upsertMealOption(@Valid @RequestBody MealOptionDTO mealOption) {
        mealOptionService.save(modelMapper.map(mealOption, MealOption.class));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value =  "/{id}", method = RequestMethod.GET)
    public MealOptionDTO getMeal(@PathVariable Long id) {
        return modelMapper.map(mealOptionService.find(id), MealOptionDTO.class);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value =  "/{id}", method = RequestMethod.DELETE)
    public void deleteMeal(@PathVariable Long id) {
        mealOptionService.delete(id);
    }
}
