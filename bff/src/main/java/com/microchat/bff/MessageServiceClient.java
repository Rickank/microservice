package com.microchat.bff;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class MessageServiceClient {

    private final RestClient restClient;

    public MessageServiceClient() {
        // RestClient för att anropa message-service
        this.restClient = RestClient.create("http://localhost:8082");
    }

    // Circuit breaker öppnas om 50% av anropen misslyckas
    // Fallback returnerar en tom lista om message-service är nere
    @CircuitBreaker(name = "messageService", fallbackMethod = "fallbackGetMessages")
    public List<?> getMessages() {
        return restClient.get()
                .uri("/messages")
                .retrieve()
                .body(List.class);
    }

    // Fallback-metod som körs när circuit breaker är öppen
    public List<?> fallbackGetMessages(Exception e) {
        return Collections.emptyList();
    }
}