package com.proflow.proflow.dto;

import com.proflow.proflow.model.postgres.TaskStatus;
import lombok.Data;

@Data
public class TaskStatusRequest {
    private TaskStatus status;
}