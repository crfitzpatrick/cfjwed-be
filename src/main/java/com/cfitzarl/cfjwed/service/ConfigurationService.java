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

import com.cfitzarl.cfjwed.data.enums.ConfigKey;
import com.cfitzarl.cfjwed.data.model.Config;

import java.util.List;

/**
 * This handles configuration-related logic.
 */
public interface ConfigurationService {

    /**
     * Returns all persisted {@link Config}s.
     *
     * @return all configs
     */
    List<Config> find();

    /**
     * This returns a {@link Config} by its {@link ConfigKey}.
     *
     * @param configKey the key
     * @return the config
     */
    Config findByKey(ConfigKey configKey);

    /**
     * This saves a {@link Config} entry.
     *
     * @param config the config entry to save
     * @return the saved config entry
     */
    Config save(Config config);

}
