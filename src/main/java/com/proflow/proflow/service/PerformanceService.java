package com.proflow.proflow.service;

import com.proflow.proflow.dto.PerformanceResponse;
import com.proflow.proflow.repository.mongo.PerformanceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceLogRepository performanceLogRepository;

    public List<PerformanceResponse> getLogs() {
        return performanceLogRepository.findAll().stream()
                .map(log -> new PerformanceResponse(
                        log.getOperation(),
                        log.getPostgresTimeMs(),
                        log.getMongoTimeMs(),
                        log.getTimestamp()
                )).toList();
    }

    public List<PerformanceResponse> getByOperation(String operation) {
        return performanceLogRepository.findByOperation(operation).stream()
                .map(log -> new PerformanceResponse(
                        log.getOperation(),
                        log.getPostgresTimeMs(),
                        log.getMongoTimeMs(),
                        log.getTimestamp()
                )).toList();
    }
}