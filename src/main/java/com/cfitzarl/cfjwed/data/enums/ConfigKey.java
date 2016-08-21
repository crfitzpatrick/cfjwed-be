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

package com.cfitzarl.cfjwed.data.enums;

import java.util.HashMap;
import java.util.Map;

public enum ConfigKey {
    CEREMONY_ADDRESS("event.address.ceremony"),
    DATE("event.date"),
    DESCRIPTION("event.description"),
    DRESS_CODE("event.dress.code"),
    EMAIL("event.email"),
    RECEPTION_ADDRESS("event.address.reception"),
    TIME("event.time"),
    TITLE("event.title"),
    URL("event.url");

    private String translationKey;
    private static Map<String, ConfigKey> LOCALE_MAPPINGS = new HashMap<>();

    static {
        for (ConfigKey key : values()) {
            LOCALE_MAPPINGS.put(key.getTranslationKey(), key);
        }
    }

    public static ConfigKey fromTranslationKey(String key) {
        return LOCALE_MAPPINGS.get(key);
    }

    ConfigKey(String key) {
        this.translationKey = key;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}