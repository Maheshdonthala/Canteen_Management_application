package com.jsp.canteen_management_system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "salaries")
@Data
public class Salary {
    @Id
    private String id;
    private String workerId;
    private String month;
    private int year;
    private String status; // e.g., "PAID", "PENDING"
}
