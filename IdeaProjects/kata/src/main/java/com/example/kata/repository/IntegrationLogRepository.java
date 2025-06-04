package com.example.kata.repository;

import com.example.kata.entity.IntegrationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Long> {
    List<IntegrationLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    List<IntegrationLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}