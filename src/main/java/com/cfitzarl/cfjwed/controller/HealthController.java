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

import com.cfitzarl.cfjwed.data.enums.ConfigKey;
import com.cfitzarl.cfjwed.service.ConfigurationService;
import com.cfitzarl.cfjwed.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

/**
 * This contains a single API that allows the frontend to check the health of the backend.
 */
@Controller
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private RedisService redisService;

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);

    /**
     * This attempts to hit the database and redis with simple calls, and will return a 502 if that isn't possible.
     *
     * @param response the servlet response
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public void checkHealth(HttpServletResponse response) {
        try {
            configurationService.findByKey(ConfigKey.EMAIL);
            redisService.get("health");
        } catch (Exception e) {
            LOGGER.error("CRITICAL: Service outage detected", e);
            response.setStatus(502);
        }
    }
}
