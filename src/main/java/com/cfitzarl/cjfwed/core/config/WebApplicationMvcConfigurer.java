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

package com.cfitzarl.cjfwed.core.config;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@EnableWebMvc
@Configuration
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebApplicationMvcConfigurer extends WebMvcConfigurerAdapter {

    @Value("${jedis.host}")
    private String jedisHost;

    @Value("${jedis.port}")
    private Integer jedisPort;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**").addResourceLocations("/assets");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer configurePropertiesLoader() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(true);
        configurer.setLocations(
                new ClassPathResource("application.properties"),
                new FileSystemResource("/usr/local/cfjwed/application.properties")
        );
        return configurer;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
            "classpath:locales/events", "classpath:locales/emails", "classpath:locales/errors"
        );
        return messageSource;
    }

    @Bean(name = "emailTaskExecutor")
    public ThreadPoolTaskExecutor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(5);
        return executor;
    }

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool(jedisHost, jedisPort);
    }

    @Bean
    public VelocityEngine velocityEngine() {
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty("resource.loader", "class");
        velocityProperties.setProperty(
            "class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
        );

        VelocityEngineFactory engineFactory = new VelocityEngineFactory();
        engineFactory.setVelocityProperties(velocityProperties);

        VelocityEngine engine;

        try {
            engine = engineFactory.createVelocityEngine();
        } catch (IOException e) {
            throw new VelocityException(e);
        }

        return engine;
    }

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        return new JavaMailSenderImpl();
    }
}
