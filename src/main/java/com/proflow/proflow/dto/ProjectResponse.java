package com.proflow.proflow.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerName;
    private List<String> memberNames;
    private LocalDateTime createdAt;
}