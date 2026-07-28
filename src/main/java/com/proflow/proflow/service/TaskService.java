package com.proflow.proflow.service;

import com.proflow.proflow.dto.TaskRequest;
import com.proflow.proflow.dto.TaskResponse;
import com.proflow.proflow.model.mongo.PerformanceLog;
import com.proflow.proflow.model.mongo.TaskDocument;
import com.proflow.proflow.model.postgres.Task;
import com.proflow.proflow.model.postgres.TaskStatus;
import com.proflow.proflow.repository.mongo.PerformanceLogRepository;
import com.proflow.proflow.repository.mongo.TaskDocumentRepository;
import com.proflow.proflow.repository.postgres.ProjectRepository;
import com.proflow.proflow.repository.postgres.TaskRepository;
import com.proflow.proflow.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskDocumentRepository taskDocumentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PerformanceLogRepository performanceLogRepository;

    public TaskResponse create(Long projectId, TaskRequest request, String userEmail) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        var assignee = request.getAssigneeId() != null
                ? userRepository.findById(request.getAssigneeId()).orElse(null)
                : null;

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.TODO)
                .assignee(assignee)
                .project(project)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AtomicLong pgTime = new AtomicLong();
        AtomicLong mongoTime = new AtomicLong();
        final Task[] savedTask = new Task[1];

        CompletableFuture<Void> pgFuture = CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();
            savedTask[0] = taskRepository.save(task);
            pgTime.set(System.currentTimeMillis() - start);
        });

        CompletableFuture<Void> mongoFuture = CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();
            TaskDocument doc = TaskDocument.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .status(TaskStatus.TODO.name())
                    .assigneeEmail(assignee != null ? assignee.getEmail() : null)
                    .projectId(projectId)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            taskDocumentRepository.save(doc);
            mongoTime.set(System.currentTimeMillis() - start);
        });

        CompletableFuture.allOf(pgFuture, mongoFuture).join();

        performanceLogRepository.save(PerformanceLog.builder()
                .operation("CREATE_TASK")
                .postgresTimeMs(pgTime.get())
                .mongoTimeMs(mongoTime.get())
                .timestamp(LocalDateTime.now())
                .build());

        return toResponse(savedTask[0]);
    }

    public List<TaskResponse> getByProject(Long projectId) {
        AtomicLong pgTime = new AtomicLong();
        AtomicLong mongoTime = new AtomicLong();

        CompletableFuture<List<Task>> pgFuture = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            List<Task> tasks = taskRepository.findByProjectId(projectId);
            pgTime.set(System.currentTimeMillis() - start);
            return tasks;
        });

        CompletableFuture<Void> mongoFuture = CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();
            taskDocumentRepository.findByProjectId(projectId);
            mongoTime.set(System.currentTimeMillis() - start);
        });

        List<Task> tasks = pgFuture.join();
        mongoFuture.join();

        performanceLogRepository.save(PerformanceLog.builder()
                .operation("LIST_TASKS")
                .postgresTimeMs(pgTime.get())
                .mongoTimeMs(mongoTime.get())
                .timestamp(LocalDateTime.now())
                .build());

        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskResponse updateStatus(Long taskId, TaskStatus status) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return toResponse(taskRepository.save(task));
    }

    public void delete(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private TaskResponse toResponse(Task t) {
        TaskResponse r = new TaskResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setStatus(t.getStatus().name());
        r.setAssigneeName(t.getAssignee() != null ? t.getAssignee().getName() : null);
        r.setProjectId(t.getProject().getId());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        return r;
    }
}