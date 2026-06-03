package com.microchat.bff;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
public class UserInfoController {

    private final RestClient restClient;

    public UserInfoController() {
        // RestClient för att anropa user-service
        this.restClient = RestClient.create("http://localhost:8081");
    }

    // Returnerar inloggad användares namn och roll
    @GetMapping("/me")
    public Map<String, String> getCurrentUser(@AuthenticationPrincipal OidcUser user) {
        String username = user.getName();

        try {
            // Hämta användaren från user-service för att få rollen
            Map userInfo = restClient.get()
                    .uri("/users/by-username/{username}", username)
                    .retrieve()
                    .body(Map.class);

            String role = (String) userInfo.get("role");
            return Map.of("username", username, "role", role != null ? role : "USER");
        } catch (Exception e) {
            return Map.of("username", username, "role", "USER");
        }
    }
}