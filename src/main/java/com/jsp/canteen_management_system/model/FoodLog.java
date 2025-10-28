package com.jsp.canteen_management_system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jsp.canteen_management_system.enums.MealType;

import java.time.LocalDateTime;

@Document(collection = "food_logs")
@Data
public class FoodLog {
    @Id
    private String id;
    private LocalDateTime timestamp;
    private int platesSold;
    private int platesBought; // number of plates bought/produced for the meal (inventory in)
    private double pricePerPlate;
    private MealType mealType; 
    private String canteenId;
}
