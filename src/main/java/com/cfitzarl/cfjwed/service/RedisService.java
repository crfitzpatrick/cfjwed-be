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

/**
 * This is an abstraction layer on top of Jedis to make interactions easier.
 */
public interface RedisService {

    /**
     * Returns whether the given key exists.
     *
     * @param key the key to check
     * @return whether an entry exists with the id
     */
    boolean exists(String key);

    /**
     * This updates the TTL on the given entry by its key.
     *
     * @param key the key of the entry
     * @param expiration the TTL in seconds
     */
    void expire(String key, int expiration);

    /**
     * Returns the value of an entry by its key.
     *
     * @param key the key to lookup
     * @return the value of the entry
     */
    String get(String key);

    /**
     * Sets a key-value pair. It will overwrite existing records.
     *
     * @param key the key to use
     * @param value the value to use
     */
    void set(String key, String value);
}
