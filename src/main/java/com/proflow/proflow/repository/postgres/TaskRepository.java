package com.proflow.proflow.repository.postgres;

import com.proflow.proflow.model.postgres.Task;
import com.proflow.proflow.model.postgres.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
    List<Task> findByAssigneeId(Long userId);
}