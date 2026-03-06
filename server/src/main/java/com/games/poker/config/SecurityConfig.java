package com.games.poker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.games.poker.auth.JwtAuthEntryPoint;
import com.games.poker.auth.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	JwtAuthenticationFilter authenticationFilter;
	
	@Autowired
	JwtAuthEntryPoint jwtAuthEntryPoint;
	

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
        .csrf(configure -> configure.disable())
        .authorizeHttpRequests((auth) -> {
            auth
            .requestMatchers("/poker/auth/register", "/poker/auth/login").permitAll()
            .requestMatchers(HttpMethod.GET,"/poker/**").authenticated()
            .requestMatchers(HttpMethod.POST,"/poker/**").authenticated()
            .requestMatchers(HttpMethod.PUT,"/poker/**").authenticated()
            .requestMatchers(HttpMethod.PATCH,"/poker/**").authenticated()
            .requestMatchers(HttpMethod.DELETE,"/poker/**").authenticated()
            .anyRequest().denyAll();

        })
        .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthEntryPoint)
        )
        .addFilterBefore(authenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
	
}