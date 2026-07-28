package com.proflow.proflow.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "performance_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PerformanceLog {

    @Id
    private String id;
    private String operation;
    private Long postgresTimeMs;
    private Long mongoTimeMs;
    private LocalDateTime timestamp;
}