package com.proflow.proflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PerformanceResponse {
    private String operation;
    private Long postgresTimeMs;
    private Long mongoTimeMs;
    private LocalDateTime timestamp;
}