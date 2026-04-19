package com.kata.release.controller;

import com.kata.release.dto.CreateReleaseDTO;
import com.kata.release.dto.ReleaseResponseDTO;
import com.kata.release.service.ReleaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/releases")
public class ReleaseController {

    private final ReleaseService releaseService;

    public ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    @PostMapping
    public ResponseEntity<ReleaseResponseDTO> create(@Valid @RequestBody CreateReleaseDTO dto) {
        ReleaseResponseDTO response = releaseService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReleaseResponseDTO>> findAll() {
        return ResponseEntity.ok(releaseService.findAll());
    }
}
