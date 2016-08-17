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

package com.cfitzarl.cjfwed.controller;

import com.cfitzarl.cjfwed.exception.BadRequestException;
import com.cfitzarl.cjfwed.exception.DuplicateEmailException;
import com.cfitzarl.cjfwed.exception.ResourceNotFoundException;
import com.cfitzarl.cjfwed.exception.UnauthorizedException;
import com.cfitzarl.cjfwed.service.LocalizationService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This intercepts exceptions that leak up to the controller-layer and provides an appropriate response
 * code and message. This is also autowired into the authentication layer as that does not run within the
 * same context as the rest of the app, so exceptions thrown there will not be intercepted by this automatically.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    @Autowired
    private LocalizationService localizationService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleResourceNotFoundExceptions(ResourceNotFoundException e, HttpServletResponse response) {
        respond(e, "errors.not.found", response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MappingException.class, BadRequestException.class, IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public void handleBadRequestExceptions(Exception e, HttpServletResponse response) throws IOException {
        respond(e, "errors.bad.data", response);
    }

    @ExceptionHandler({AuthenticationException.class, UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUnauthorizedExceptions(Exception e, HttpServletResponse response) {
        respond(e, "errors.unauthorized", response);
    }

    @ExceptionHandler({BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleBadCredsException(BadCredentialsException e, HttpServletResponse response) {
        respond(e, "errors.creds", response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleAccessDeniedExceptions(AccessDeniedException e, HttpServletResponse response) {
        respond(e, "errors.forbidden", response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleDuplicateEmailException(DuplicateEmailException e, HttpServletResponse response) {
        respond(e, "errors.duplicate.email", response);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleGenericException(Exception e, HttpServletResponse response) {
        respond(e, "errors.generic", response, true);
    }

    /**
     * This logs the exception and forms an {@link ErrorResponse} object. The message provided during construction
     * is localized using the {@link LocalizationService} and the "errors" localization bundle.
     *
     * @param e the exception
     * @param messageKey the localization key
     * @param response the response
     */
    public void respond(Exception e, String messageKey, HttpServletResponse response) {
        respond(e, messageKey, response, false);
    }

    /**
     * This logs the exception with a configurable level and forms an {@link ErrorResponse} object. The message
     * provided during construction is localized using the {@link LocalizationService} and the "errors" localization
     * bundle.
     *
     * @param e the exception
     * @param messageKey the localization key
     * @param response the response
     * @param verbose whether to use verbose logging
     */
    private void respond(Exception e, String messageKey, HttpServletResponse response, boolean verbose) {
        if (verbose) {
            LOGGER.error("Error handling request", e);
        }

        String localizedMsg = localizationService.getMessage(messageKey);
        ErrorResponse error = new ErrorResponse(localizedMsg);

        try {
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(error));
        } catch (IOException io) {
            LOGGER.error("Error handling error request", io);
        }
    }

    @Data
    private class ErrorResponse {

        @JsonProperty("msg")
        private String msg;

        public ErrorResponse() { }

        public ErrorResponse(String msg) {
            this.msg = msg;
        }
    }
}
