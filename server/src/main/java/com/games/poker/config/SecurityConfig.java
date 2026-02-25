package com.games.poker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;


@Configuration
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    	 http
//         .csrf(configure -> configure.disable())
//         .authorizeHttpRequests((auth) -> {
//             auth
//             .requestMatchers("/poker/**").permitAll()
//             .requestMatchers(HttpMethod.OPTIONS).permitAll()  // allow preflight
//             .anyRequest().denyAll();
//
//         });
//
//        return http.build();
//    }
}