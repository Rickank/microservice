package com.microchat.message_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MessageServiceApplicationTests {

    @Autowired
    private WebApplicationContext context;

    // Mockar RabbitMQ
    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    // Testar att GET /messages returnerar en tom lista från början
    @Test
    void getAllMessages_shouldReturnEmptyList() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // Testar att man kan skicka ett meddelande via POST /messages
    @Test
    void sendMessage_shouldReturnSavedMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sender\":\"admin\",\"content\":\"hej\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender").value("admin"))
                .andExpect(jsonPath("$.content").value("hej"));
    }

    // Testar att meddelandet sparas i databasen
    @Test
    void sendMessage_shouldPersistInDatabase() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sender\":\"admin\",\"content\":\"testmeddelande\"}"));

        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("testmeddelande"));
    }

    // Testar att timestamp sätts automatiskt
    @Test
    void sendMessage_shouldSetTimestampAutomatically() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sender\":\"admin\",\"content\":\"hej\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}