package com.jsp.canteen_management_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "canteens") // Tells MongoDB to store this in a collection named "canteens"
@Data // Lombok annotation to automatically create getters, setters, etc.
public class Canteen {

    @Id
    private String id;

    private String name;
    private String location;
    private double defaultPlatePrice = 5.0; 
    
}


