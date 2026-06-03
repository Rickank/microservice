package com.microchat.user_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    // Skapar admin-användaren vid uppstart om den inte redan finns
    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(new BCryptPasswordEncoder().encode("password"));
                userRepository.save(admin);
                System.out.println("Admin-användare skapad!");
            }
        };
    }
}