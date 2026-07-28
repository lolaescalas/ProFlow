package com.proflow.proflow.repository.mongo;

import com.proflow.proflow.model.mongo.TaskDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskDocumentRepository extends MongoRepository<TaskDocument, String> {
    List<TaskDocument> findByProjectId(Long projectId);
    List<TaskDocument> findByStatus(String status);
}