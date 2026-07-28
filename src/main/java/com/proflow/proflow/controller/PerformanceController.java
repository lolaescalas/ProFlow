package com.proflow.proflow.controller;

import com.proflow.proflow.dto.PerformanceResponse;
import com.proflow.proflow.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping
    public ResponseEntity<List<PerformanceResponse>> getLogs() {
        return ResponseEntity.ok(performanceService.getLogs());
    }

    @GetMapping("/{operation}")
    public ResponseEntity<List<PerformanceResponse>> getByOperation(
            @PathVariable String operation) {
        return ResponseEntity.ok(performanceService.getByOperation(operation));
    }
}