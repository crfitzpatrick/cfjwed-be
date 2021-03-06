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

package com.cfitzarl.cfjwed.service;

import com.cfitzarl.cfjwed.data.model.MealOption;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.UUID;

/**
 * This provides a service layer abstraction around {@link MealOption} data.
 */
public interface MealOptionService {

    /**
     * This deletes a {@link MealOption} by its primary key.
     *
     * @param id the primary key
     */
    void delete(UUID id);

    /**
     * This returns all {@link MealOption} data.
     *
     * @return a list of meals
     */
    List<MealOption> find();

    /**
     * This returns a {@link MealOption} from its primary key.
     *
     * @param id the primary key
     * @return the meal
     */
    MealOption find(UUID id);

    /**
     * This returns the total count of {@link com.cfitzarl.cfjwed.data.model.Attendant}s that have chosen each
     * available {@link MealOption}.
     *
     * @return the total count
     */
    List<Pair<MealOption, Long>> getChosenMealCount();

    /**
     * This saves a {@link MealOption}.
     *
     * @param mealOption the meal
     */
    void save(MealOption mealOption);
}
