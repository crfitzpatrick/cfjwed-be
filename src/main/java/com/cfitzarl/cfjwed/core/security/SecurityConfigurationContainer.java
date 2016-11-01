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

package com.cfitzarl.cfjwed.core.security;

import com.cfitzarl.cfjwed.controller.ApiExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfigurationContainer extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProcessingFilter authenticationProcessingFilter;

    @Autowired
    private CsrfValidatingFilter csrfValidatingFilter;

    @Autowired
    private AccessDeniedEntryPointHandler pointHandler;

    @Autowired
    private SecurityContextLoader securityContextRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(authenticationProcessingFilter, ExceptionTranslationFilter.class)
            .addFilterAfter(csrfValidatingFilter, authenticationProcessingFilter.getClass())
            .exceptionHandling()
                .accessDeniedHandler(pointHandler)
                .authenticationEntryPoint(pointHandler)
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
            .securityContext()
                .securityContextRepository(securityContextRepository)
                .and()
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/api/accounts/data")
                    .permitAll()
                .antMatchers("/api/activations/**")
                    .permitAll()
                .antMatchers("/api/health")
                    .permitAll()
                .antMatchers("/**")
                    .fullyAuthenticated();
    }

    /**
     * This acts as both an {@link AccessDeniedHandler} and an {@link AuthenticationEntryPoint}.
     */
    @Component
    private static class AccessDeniedEntryPointHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

        @Autowired
        private ApiExceptionHandler exceptionHandler;

        @Override
        public void handle(HttpServletRequest request,
                           HttpServletResponse response,
                           AccessDeniedException e) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            exceptionHandler.handleAccessDeniedExceptions(e, response);
        }

        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthenticationException e) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            exceptionHandler.handleUnauthorizedExceptions(e, response);
        }
    }
}
