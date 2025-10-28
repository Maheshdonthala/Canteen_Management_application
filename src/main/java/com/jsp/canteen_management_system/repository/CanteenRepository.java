package com.jsp.canteen_management_system.repository;

import com.jsp.canteen_management_system.model.Canteen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanteenRepository extends MongoRepository<Canteen, String> {
    // Spring Data MongoDB automatically creates all the database methods for you!
}
