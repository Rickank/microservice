package com.microchat.bff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
public class BffConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Kräv inloggning för alla endpoints
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                // Omdirigera till klienten efter lyckad inloggning
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/", true)
                )
                .oauth2Client(Customizer.withDefaults());
        return http.build();
    }

    // Vidarebefordra /api/users till user-service
    @Bean
    public RouterFunction<ServerResponse> userRoute() {
        return route()
                .path("/api/users", builder -> builder
                        .GET("/**", http())
                        .POST("/**", http())
                        .DELETE("/**", http())
                )
                .before(uri("http://localhost:8081/"))
                .before(setPath("/users"))
                .filter(tokenRelay())
                .build();
    }

    // Vidarebefordra /api/messages till message-service
    @Bean
    public RouterFunction<ServerResponse> messageRoute() {
        return route()
                .path("/api/messages", builder -> builder
                        .GET("/**", http())
                        .POST("/**", http())
                )
                .before(uri("http://localhost:8082/"))
                .before(setPath("/messages"))
                .filter(tokenRelay())
                .build();
    }
}