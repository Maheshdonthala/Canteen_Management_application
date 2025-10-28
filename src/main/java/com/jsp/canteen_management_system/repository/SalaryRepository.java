package com.jsp.canteen_management_system.repository;

import com.jsp.canteen_management_system.model.Salary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRepository extends MongoRepository<Salary, String> {
    long countByStatus(String status);
}
