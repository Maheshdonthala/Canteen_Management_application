package com.jsp.canteen_management_system.repository;

import com.jsp.canteen_management_system.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Add custom queries if needed
    java.util.Optional<User> findByUsername(String username);
}
