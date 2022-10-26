package com.example.ecom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().disable() // block strange domain
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll().anyRequest().authenticated();
        return http.build();
    }
}
