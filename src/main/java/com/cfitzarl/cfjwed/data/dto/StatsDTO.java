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

package com.cfitzarl.cfjwed.data.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object used to provide statistical information to the UI.
 */
@Data
public class StatsDTO {
    private long pendingAttendants;
    private long acceptedAttendants;
    private long declinedAttendants;

    private List<MealOptionEntry> mealStats = new ArrayList<>();

    public void addMealStat(String name, Long count) {
        mealStats.add(new MealOptionEntry(name, count));
    }

    @Data
    static class MealOptionEntry {
        private String name;
        private Long count;

        MealOptionEntry(String name, Long count) {
            this.name = name;
            this.count = count;
        }
    }
}
