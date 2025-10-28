package com.jsp.canteen_management_system.repository;

import com.jsp.canteen_management_system.model.FoodLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodLogRepository extends MongoRepository<FoodLog, String> {
    // Custom queries for FoodLog can be added here
    java.util.List<FoodLog> findByTimestampBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
