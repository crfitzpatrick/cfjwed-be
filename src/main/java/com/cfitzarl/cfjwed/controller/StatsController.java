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

package com.cfitzarl.cfjwed.controller;

import com.cfitzarl.cfjwed.data.dto.StatsDTO;
import com.cfitzarl.cfjwed.data.enums.ResponseStatus;
import com.cfitzarl.cfjwed.data.model.MealOption;
import com.cfitzarl.cfjwed.service.AttendantService;
import com.cfitzarl.cfjwed.service.MealOptionService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This provides statistical APIs for data analysis.
 */
@Controller
@ResponseBody
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private AttendantService attendantService;

    @Autowired
    private MealOptionService mealOptionService;

    /**
     * This returns four different statistics back to the browser:
     *
     * <ul>
     *    <li>The number of attendants who have accepted</li>
     *    <li>The number of attendants who have declined</li>
     *    <li>The number of attendants who have not yet responded</li>
     *    <li>A breakdown of each meal and how many people have chosen them</li>
     * </ul>
     *
     * @return the stats
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public StatsDTO displayStats() {

        StatsDTO dto = new StatsDTO();
        dto.setAcceptedAttendants(attendantService.countByStatus(ResponseStatus.ACCEPTED));
        dto.setDeclinedAttendants(attendantService.countByStatus(ResponseStatus.DECLINED));
        dto.setPendingAttendants(attendantService.countByStatus(ResponseStatus.PENDING));

        for (Pair<MealOption, Long> mealGroupings : mealOptionService.getChosenMealCount()) {
            dto.addMealStat(mealGroupings.getLeft().getName(), mealGroupings.getRight());
        }

        return dto;
    }

}
