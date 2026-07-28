package com.proflow.proflow.repository.mongo;

import com.proflow.proflow.model.mongo.PerformanceLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PerformanceLogRepository extends MongoRepository<PerformanceLog, String> {
    List<PerformanceLog> findByOperation(String operation);
}