package com.kata.rules.controller;

import com.kata.rules.dto.EvaluationResultDTO;
import com.kata.rules.dto.ReleaseRequestDTO;
import com.kata.rules.service.RulesEvaluatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rules")
public class RulesController {

    private final RulesEvaluatorService evaluatorService;

    public RulesController(RulesEvaluatorService evaluatorService) {
        this.evaluatorService = evaluatorService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluationResultDTO> evaluate(@RequestBody ReleaseRequestDTO request) {
        EvaluationResultDTO result = evaluatorService.evaluate(request);
        return ResponseEntity.ok(result);
    }
}
