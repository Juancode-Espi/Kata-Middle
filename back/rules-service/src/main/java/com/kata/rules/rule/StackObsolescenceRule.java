package com.kata.rules.rule;

import com.kata.rules.dto.ReleaseRequestDTO;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;

import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StackObsolescenceRule implements ReleaseRule {

    private final RestTemplate restTemplate;

    public StackObsolescenceRule(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final Logger log = LoggerFactory.getLogger(StackObsolescenceRule.class);

    private static final Set<String> OBSOLETE_KEYWORDS = Set.of(
            "java 8", "java 7", "java 6", "spring boot 1", "spring 4", "angularjs", "python 2"
    );

    @Override
    public String evaluate(ReleaseRequestDTO request) {
        if (request.stack() == null || request.stack().isBlank()) {
            return null;
        }
        String stack = request.stack().toLowerCase().trim();
        log.info("[RulesService] Checking obsolescence for stack: {}", stack);

        for (String obsolete : OBSOLETE_KEYWORDS) {
            if (stack.contains(obsolete)) {
                log.warn("[RulesService] Obsolete technology detected (local): {}", obsolete);
                return "Stack obsoleto (detectado localmente): '" + obsolete + "'";
            }
        }

        if (stack.contains("java")) {
            Pattern p = Pattern.compile("java\\s*(\\d+)");
            Matcher m = p.matcher(stack);
            if (m.find()) {
                String version = m.group(1);
                log.info("[RulesService] Detected Java version: {}. Verifying with endoflife.date...", version);
                try {
                    String url = "https://endoflife.date/api/java.json";
                    List<Map<String, Object>> releases = restTemplate.getForObject(url, List.class);
                    if (releases != null) {
                        for (Map<String, Object> release : releases) {
                            String cycle = String.valueOf(release.get("cycle"));
                            if (version.equals(cycle)) {
                                Object eolObj = release.get("eol");
                                log.info("[RulesService] Java {} EOL data: {}", cycle, eolObj);
                                if (eolObj instanceof String eol) {
                                    if (isPastDate(eol)) {
                                        return "Versión de Java (" + version + ") obsoleta según endoflife.date (EOL: " + eol + ")";
                                    }
                                } else if (eolObj instanceof Boolean && (Boolean) eolObj) {
                                    return "Versión de Java (" + version + ") marcada como obsoleta (EOL: true)";
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("[RulesService] Error calling endoflife.date: {}", e.getMessage());
                }
            }
        }

        return null;
    }

    private boolean isPastDate(String dateStr) {
        try {
            return java.time.LocalDate.parse(dateStr).isBefore(java.time.LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}
