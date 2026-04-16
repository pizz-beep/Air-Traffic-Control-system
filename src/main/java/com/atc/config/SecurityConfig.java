package com.atc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.*
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
            .requestMatchers("/users/login", "/css/**").permitAll()
            .requestMatchers("/flights/submit").hasRole("PILOT")
            .requestMatchers("/flights/*/approve",
                             "/flights/*/reject",
                             "/runways/**").hasRole("ATC_CONTROLLER")
            .requestMatchers("/users/**").hasRole("ADMINISTRATOR")
            .anyRequest().authenticated()
          )
          .formLogin(form -> form
            .loginPage("/users/login")
            .defaultSuccessUrl("/flights", true)
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


9. application.properties
# Server
server.port=8080

# H2 In-Memory DB (swap for MySQL/PostgreSQL in production)
spring.datasource.url=jdbc:h2:mem:atcdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Thymeleaf
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false


