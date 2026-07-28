package com.proflow.proflow.dto;

import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Long assigneeId;
}