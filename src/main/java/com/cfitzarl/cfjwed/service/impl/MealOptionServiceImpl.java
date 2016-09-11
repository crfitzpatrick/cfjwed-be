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

package com.cfitzarl.cfjwed.service.impl;

import com.cfitzarl.cfjwed.data.dao.AttendantDao;
import com.cfitzarl.cfjwed.data.dao.MealOptionDao;
import com.cfitzarl.cfjwed.data.model.MealOption;
import com.cfitzarl.cfjwed.service.MealOptionService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MealOptionServiceImpl implements MealOptionService {

    @Autowired
    private AttendantDao attendantDao;

    @Autowired
    private MealOptionDao mealOptionDao;

    /** {@inheritDoc} **/
    @Override
    public void delete(UUID id) {
        mealOptionDao.delete(id);
    }

    /** {@inheritDoc} **/
    @Override
    public List<MealOption> find() {
        return mealOptionDao.findAll();
    }

    /** {@inheritDoc} **/
    @Override
    public MealOption find(UUID id) {
        return mealOptionDao.findOne(id);
    }

    /** {@inheritDoc} **/
    @Override
    public List<Pair<MealOption, Long>> getChosenMealCount() {
        List<Pair<MealOption, Long>> mealGroupings = new ArrayList<>();

        for (MealOption option : find()) {
            mealGroupings.add(new ImmutablePair<>(option, attendantDao.countByMeal(option)));
        }

        return mealGroupings;
    }

    /** {@inheritDoc} **/
    @Override
    public void save(MealOption mealOption) {
        mealOptionDao.save(mealOption);
    }
}
