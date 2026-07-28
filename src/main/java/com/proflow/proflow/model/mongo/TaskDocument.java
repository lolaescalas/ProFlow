package com.proflow.proflow.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskDocument {

    @Id
    private String id;
    private String title;
    private String description;
    private String status;
    private String assigneeEmail;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}