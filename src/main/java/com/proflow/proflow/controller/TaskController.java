package com.proflow.proflow.controller;

import com.proflow.proflow.dto.TaskRequest;
import com.proflow.proflow.dto.TaskResponse;
import com.proflow.proflow.dto.TaskStatusRequest;
import com.proflow.proflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @PathVariable Long projectId,
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(taskService.create(projectId, request, email));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getByProject(projectId));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody TaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, request.getStatus()));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}