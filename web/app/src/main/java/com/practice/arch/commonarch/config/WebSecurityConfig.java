/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

package com.practice.arch.commonarch.config;

import com.google.common.collect.ImmutableList;
import com.practice.arch.commonarch.component.GlobalExceptionHandler;
import com.practice.arch.commonarch.component.jwt.JwtAuthenticationProvider;
import com.practice.arch.commonarch.component.jwt.TokenAuthenticationFilter;
import com.practice.arch.commonarch.component.redis.ratelimiter.RateLimiterInterceptor;
import com.practice.arch.commonarch.constants.Config;
import com.practice.arch.commonarch.service.TokenService;
import com.practice.arch.commonarch.service.UserService;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

/**
 * Created by byang059 on 5/12/20
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    UserService userService;

    @Autowired
    GlobalExceptionHandler exceptionHandler;

    @Autowired
    AuthenticationEntryPoint entryPoint;

    @Autowired
    TokenService tokenService;

    @Autowired
    RateLimiterInterceptor rateLimiterInterceptor;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //禁用跨域
        http.csrf().disable().cors();

        //url权限限制
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(Config.PERMIT_URLS.toArray(new String[] {})).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .anyRequest().authenticated();
        //异常处理
        http.exceptionHandling().authenticationEntryPoint(exceptionHandler).accessDeniedHandler(exceptionHandler);
        //禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //添加jwt filter
        http.addFilterBefore(new TokenAuthenticationFilter(entryPoint, authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class).authenticationProvider(jwtAuthenticationProvider());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimiterInterceptor);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }

    /**
     * fix Cors issue for spring security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public Key getJwtSecretKey() {
        ClassPathResource classPathResource = new ClassPathResource("private.key");
        try {
            InputStream inputStream = classPathResource.getInputStream();
            byte[] bytes = IOUtils.readFully(inputStream, inputStream.available());
            return Keys.hmacShaKeyFor(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
