package com.jsp.canteen_management_system.repository;

import com.jsp.canteen_management_system.model.Worker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepository extends MongoRepository<Worker, String> {
    // additional query methods can be added later
}
