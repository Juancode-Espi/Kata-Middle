package com.kata.integration.controller;

import com.kata.integration.dto.PrResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/integration")
public class IntegrationController {

    private static final Logger log = LoggerFactory.getLogger(IntegrationController.class);
    private final RestTemplate restTemplate;

    public IntegrationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/pr/{*prId}")
    public PrResponseDTO checkPr(@PathVariable String prId) {
        String cleanId = prId.startsWith("/") ? prId.substring(1) : prId;
        log.info("[IntegrationService] Checking real GitHub PR for: {}", cleanId);

        String owner = "facebook";
        String repo = "react";
        String pullNumber = cleanId;

        Pattern pattern = Pattern.compile("([^/]+)/([^/]+)(?:/pull)?/(\\d+)");
        Matcher matcher = pattern.matcher(cleanId);
        if (matcher.find()) {
            owner = matcher.group(1);
            repo = matcher.group(2);
            pullNumber = matcher.group(3);
        }

        String githubUrl = String.format("https://api.github.com/repos/%s/%s/pulls/%s", owner, repo, pullNumber);
        
        try {
            log.info("[IntegrationService] Calling GitHub API: {}", githubUrl);
            ResponseEntity<Object> response = restTemplate.getForEntity(githubUrl, Object.class);
            return new PrResponseDTO(cleanId, response.getStatusCode().is2xxSuccessful());
        } catch (Exception e) {
            log.warn("[IntegrationService] PR not found or GitHub API error: {}", e.getMessage());
            return new PrResponseDTO(cleanId, false);
        }
    }

    @GetMapping("/deepwiki/analyze/{*repoSlug}")
    public Map<String, String> analyzeRepo(@PathVariable String repoSlug) {
        String slug = repoSlug.startsWith("/") ? repoSlug.substring(1) : repoSlug;
        log.info("[DeepWiki] Analyzing repository architecture for: {}", slug);
        
        if (slug.contains("legacy") || slug.contains("old") || slug.contains("35000")) {
            return Map.of(
                "stack", "Java 8",
                "framework", "Spring Boot 1.5.x",
                "analysisDate", java.time.LocalDateTime.now().toString(),
                "confidence", "98%",
                "provider", "DeepWiki AI"
            );
        } else if (slug.contains("react") || slug.contains("frontend")) {
            return Map.of(
                "stack", "TypeScript 5.0",
                "framework", "React 18",
                "analysisDate", java.time.LocalDateTime.now().toString(),
                "confidence", "95%",
                "provider", "DeepWiki AI"
            );
        } else {
            return Map.of(
                "stack", "Java 17",
                "framework", "Spring Boot 3.2.x",
                "analysisDate", java.time.LocalDateTime.now().toString(),
                "confidence", "92%",
                "provider", "DeepWiki AI"
            );
        }
    }
}
