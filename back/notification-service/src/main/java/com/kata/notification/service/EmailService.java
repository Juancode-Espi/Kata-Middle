package com.kata.notification.service;

import com.kata.notification.dto.NotificationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${notification.from:noreply@releases.local}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email notification. Failure is non-blocking — only logged.
     */
    public void send(NotificationRequestDTO request) {
        try {
            log.info("[NotificationService] Sending email to: {}, subject: {}", request.to(), request.subject());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(request.to());
            message.setSubject(request.subject());
            message.setText(request.body());
            mailSender.send(message);
            log.info("[NotificationService] Email sent successfully to: {}", request.to());
        } catch (Exception e) {
            log.error("[NotificationService] Failed to send email to {}: {}", request.to(), e.getMessage());
        }
    }
}
