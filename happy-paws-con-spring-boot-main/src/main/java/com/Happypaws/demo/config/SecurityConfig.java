package com.Happypaws.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/login"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/assets/**", "/css/**", "/js/**", "/bootstrap-icons-1.13.1/**").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}