package com.microchat.message_service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final RabbitTemplate rabbitTemplate;

    public MessageController(MessageRepository messageRepository, RabbitTemplate rabbitTemplate) {
        this.messageRepository = messageRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        Message saved = messageRepository.save(message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, saved.getContent());
        return saved;
    }
}