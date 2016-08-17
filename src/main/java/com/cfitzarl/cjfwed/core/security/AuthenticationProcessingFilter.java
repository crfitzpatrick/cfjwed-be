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

import com.cfitzarl.cjfwed.controller.ApiExceptionHandler;
import com.cfitzarl.cjfwed.data.dto.AuthenticationDTO;
import com.cfitzarl.cjfwed.data.model.Account;
import com.cfitzarl.cjfwed.exception.UnauthorizedException;
import com.cfitzarl.cjfwed.service.AccountService;
import com.cfitzarl.cjfwed.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * This contains all logic relating to authentication attempts.
 */
@Component
class AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationProcessingFilter.class);

    @Autowired
    private CustomAuthenticationManager authenticationManager;

    @Autowired
    private CustomAuthFailureHandler failureHandler;

    @Autowired
    private CustomAuthSuccessHandler successHandler;

    protected static final int SESSION_EXPIRY_SECONDS = 90 * 60;

    /**
     * Constructor that defines the API endpoint for auth requests.
     */
    private AuthenticationProcessingFilter() {
        super("/api/auth/login");
    }

    @PostConstruct
    public void setHandlers() {
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
        setAuthenticationManager(authenticationManager);
    }

    /**
     * This is invoked when an authentication attempt is requested. It will parse the data coming from the browser
     * and prepare it for the authentication manager. The returned authentication object will be picked up and delegated
     * to either the {@link CustomAuthSuccessHandler} or {@link CustomAuthFailureHandler}.
     *
     * @param request the incoming request
     * @param response the outgoing response
     * @return the authentication
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Map body = new ObjectMapper().readValue(request.getInputStream(), Map.class);
        String principal = (String) body.get("principal");
        String creds = (String) body.get("credentials");

        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(principal, creds));
    }

    /**
     * This manager is responsible for attempting authentication with the principal and creds from external source.
     */
    @Component
    private static class CustomAuthenticationManager implements AuthenticationManager {

        @Autowired
        private AccountService accountService;

        /**
         * This attempts to authenticate someone based on an authentication object provided by the
         * {@link AuthenticationProcessingFilter}. It will return null when an attempt fails without error, and will
         * throw a {@link BadCredentialsException} if an attempt fails due to an exception being thrown.
         *
         * @param authentication the authentication object to authenticate
         * @return a newly formed token
         * @throws AuthenticationException
         */
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            String principal = (String) authentication.getPrincipal();
            String creds = (String) authentication.getCredentials();

            Account account = accountService.findByEmail(principal);

            if (account != null) {
                if (!account.isActivated()) {
                    throw new UnauthorizedException("Account is not activated");
                }

                if (accountService.hasValidCreds(account, creds)) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(account.getType().toString());
                    return new UsernamePasswordAuthenticationToken(principal, null, Collections.singletonList(authority));
                }
            }

            throw new BadCredentialsException("An error occurred while processing authentication");
        }
    }

    /**
     * This class is invoked when the {@link CustomAuthenticationManager} returns an authentication token.
     */
    @Component
    private static class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

        @Autowired
        private AccountService accountService;

        @Autowired
        private RedisService redisService;

        /**
         * This performs post-authentication logic. This includes persisting the {@link AuthenticationDTO} in redis,
         * generating an authentication key, and returning data back to the browser.
         *
         * @param req the incoming request
         * @param res the outgoing response
         * @param auth the authentication token returned by the authentication manager
         * @throws IOException
         * @throws ServletException
         */
        @Override
        public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
                throws IOException, ServletException {
            ObjectMapper objectMapper = new ObjectMapper();

            // Generate random authentication key that is returned to the browser
            String authKey = RandomStringUtils.randomAlphanumeric(64);

            // Grab the account pertaining to the current principal
            Account account = accountService.findByEmail((String) auth.getPrincipal());
            GrantedAuthority authority = Iterables.getFirst(auth.getAuthorities(), null);

            // Create DTO that is returned to the browser and cached in redis
            AuthenticationDTO body = new AuthenticationDTO();
            body.setEmail(account.getEmail());
            body.setFirstName(account.getFirstName());
            body.setLastName(account.getLastName());
            body.setRole(authority.getAuthority());
            body.setToken(authKey);

            // Store the DTO for retrieval on subsequent requests
            redisService.set(authKey, new ObjectMapper().writeValueAsString(body));
            redisService.expire(authKey, SESSION_EXPIRY_SECONDS);

            res.setContentType("application/json");
            res.setStatus(201);
            res.getOutputStream().write(objectMapper.writeValueAsBytes(body));
        }
    }

    /**
     * This is invoked when the {@link CustomAuthenticationManager} fails authentication.
     */
    @Component
    private static class CustomAuthFailureHandler implements AuthenticationFailureHandler {

        @Autowired
        private ApiExceptionHandler exceptionHandler;

        /**
         * This performs failure logic that is sent to the browser.
         *
         * @param req the incoming request
         * @param res the outgoing response
         * @param e the auth exception
         * @throws IOException
         * @throws ServletException
         */
        @Override
        public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException e)
                throws IOException, ServletException {
            LOGGER.error("Authentication failure detected", e);

            if (!(e instanceof BadCredentialsException)) {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                exceptionHandler.handleGenericException(e, res);
                return;
            }

            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            exceptionHandler.handleBadCredsException((BadCredentialsException) e, res);
        }
    }
}
