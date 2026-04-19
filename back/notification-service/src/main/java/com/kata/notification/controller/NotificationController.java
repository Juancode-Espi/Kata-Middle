package com.kata.notification.controller;

import com.kata.notification.dto.NotificationRequestDTO;
import com.kata.notification.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> send(@RequestBody NotificationRequestDTO request) {
        emailService.send(request);
        return ResponseEntity.ok(Map.of("status", "sent"));
    }
}
