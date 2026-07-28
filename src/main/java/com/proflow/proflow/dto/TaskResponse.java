package com.proflow.proflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String assigneeName;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}