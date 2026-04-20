package com.atc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
          .authorizeHttpRequests(auth -> auth

            // ── Public ────────────────────────────────────────────────
            .requestMatchers("/users/login", "/css/**", "/js/**", "/images/**", "/error")
              .permitAll()

            // ── Role-dispatched dashboard ─────────────────────────────
            .requestMatchers("/dashboard").authenticated()

            // ── Flights: GET pages open to all authenticated ──────────
            .requestMatchers(HttpMethod.GET,
                "/flights", "/flights/submit", "/flights/monitor").authenticated()

            // ── Flight mutations: POST restricted ─────────────────────
            .requestMatchers(HttpMethod.POST, "/flights/submit").hasRole("PILOT")
            .requestMatchers(HttpMethod.POST, "/flights/*/approve",
                                              "/flights/*/reject",
                                              "/flights/*/status").hasRole("ATC_CONTROLLER")

            // ── Runways: GET open to all authenticated ─────────────────
            .requestMatchers(HttpMethod.GET,
                "/runways", "/runways/assign", "/runways/create").authenticated()

            // ── Runway mutations: ATC can assign/release; Admin can create/delete/status
            .requestMatchers(HttpMethod.POST, "/runways/assign",
                                              "/runways/*/release").hasRole("ATC_CONTROLLER")
            .requestMatchers(HttpMethod.POST, "/runways/create",
                                              "/runways/*/delete",
                                              "/runways/*/status").hasRole("ADMINISTRATOR")

            // ── Admin: user management ────────────────────────────────
            .requestMatchers("/users", "/users/create", "/users/**").hasRole("ADMINISTRATOR")

            // ── Catch-all ─────────────────────────────────────────────
            .anyRequest().authenticated()
          )
          .formLogin(form -> form
            .loginPage("/users/login")
            .defaultSuccessUrl("/dashboard", true)
          )
          .logout(logout -> logout
            .logoutSuccessUrl("/users/login")
          );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
