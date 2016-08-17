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

package com.cfitzarl.cjfwed.core.security;

import com.cfitzarl.cjfwed.data.dao.AccountDao;
import com.cfitzarl.cjfwed.data.dto.AuthenticationDTO;
import com.cfitzarl.cjfwed.data.model.Account;
import com.cfitzarl.cjfwed.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * This is responsible for retrieving and loading the security context established through
 * an authentication call in a previous request. Rather than rely on the servlet container to
 * store the session, Redis was chosen for both flexibility and scalability. Each request passes
 * through this code in order to retrieve its appropriate context. When not found, Spring Security
 * will treat the current request as if it were that of an anonymous user.
 */
@Component
public class SecurityContextLoader implements SecurityContextRepository {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private RedisService redisService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityContextLoader.class);

    private static final String AUTH_TOKEN_HEADER = "X-Auth-Token";

    /**
     * This method does all the heavy work in retrieving the context out of Redis. It inspects the servlet request
     * and tries to scrape the authentication token out of a header. If the header is missing or the token is not
     * found, an empty {@link SecurityContext} is returned, effectively telling Spring that the current request is
     * coming from an anonymous, unauthenticated actor.
     *
     * @param requestResponseHolder the request container
     * @return a security context
     */
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        String tokenParam = requestResponseHolder.getRequest().getHeader(AUTH_TOKEN_HEADER);
        SecurityContext securityContext = new SecurityContextImpl();

        if (tokenParam == null || !redisService.exists(tokenParam)) {
            return securityContext;
        }

        String serializedAuthData = redisService.get(tokenParam);
        AuthenticationDTO dto;

        try {
            dto = new ObjectMapper().readValue(serializedAuthData, AuthenticationDTO.class);
        } catch (IOException e) {
            LOGGER.error("Error deserializing auth DTO", e);
            return securityContext;
        }

        Account account = accountDao.findByEmail(dto.getEmail());

        Collection<GrantedAuthority> gal = Collections.singletonList(new SimpleGrantedAuthority(dto.getRole()));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(account.getId(), null, gal);

        securityContext.setAuthentication(token);

        return securityContext;
    }

    /**
     * This method is responsible for handling post-request context changes. On each request, we reset the TTL of the
     * authentication data in redis.
     *
     * @param context the context to save
     * @param request the request
     * @param response the response
     */
    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String tokenParam = request.getParameter(AUTH_TOKEN_HEADER);
        if ((context.getAuthentication() != null) && (tokenParam != null)) {
            redisService.expire(tokenParam, AuthenticationProcessingFilter.SESSION_EXPIRY_SECONDS);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String tokenParam = request.getParameter(AUTH_TOKEN_HEADER);
        return (tokenParam != null) && redisService.exists(tokenParam);
    }
}
