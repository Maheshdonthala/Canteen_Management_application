package com.jsp.canteen_management_system.service;

import com.jsp.canteen_management_system.model.Canteen;
import com.jsp.canteen_management_system.model.FoodLog;
import com.jsp.canteen_management_system.repository.CanteenRepository;
import com.jsp.canteen_management_system.repository.FoodLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class FoodService {

    @Autowired
    private CanteenRepository canteenRepository;

    @Autowired
    private FoodLogRepository foodLogRepository;

    public FoodLog recordSale(String canteenId, int platesSold) {
        // 1. Find the canteen by its ID
        Canteen canteen = canteenRepository.findById(canteenId)
            .orElseThrow(() -> new RuntimeException("Canteen not found!"));

        // 2. Get the dynamic price from that canteen
        double price = canteen.getDefaultPlatePrice();

        // 3. Create a new log with the price at the time of sale
        FoodLog log = new FoodLog();
        log.setTimestamp(LocalDateTime.now());
        log.setPlatesSold(platesSold);
        log.setPricePerPlate(price); // Use the dynamic price
        log.setCanteenId(canteenId);
        if (log.getMealType() == null) {
            // default to AFTERNOON if not provided
            log.setMealType(com.jsp.canteen_management_system.enums.MealType.AFTERNOON);
        }

        // 4. Save the log
        return foodLogRepository.save(log);
    }

    public FoodLog recordPurchase(String canteenId, int platesBought) {
        Canteen canteen = canteenRepository.findById(canteenId)
            .orElseThrow(() -> new RuntimeException("Canteen not found!"));

        FoodLog log = new FoodLog();
        log.setTimestamp(LocalDateTime.now());
        log.setPlatesBought(platesBought);
        log.setPricePerPlate(canteen.getDefaultPlatePrice());
        log.setCanteenId(canteenId);
        log.setMealType(com.jsp.canteen_management_system.enums.MealType.MORNING); // default for purchases

        return foodLogRepository.save(log);
    }

    /**
     * Calculates remaining plates for today by summing bought minus sold.
     */
    public int getRemainingPlatesToday(String canteenId) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime start = today.atStartOfDay();
        java.time.LocalDateTime end = today.atTime(java.time.LocalTime.MAX);

        java.util.List<FoodLog> logs = foodLogRepository.findByTimestampBetween(start, end);
        int bought = 0;
        int sold = 0;
        for (FoodLog l : logs) {
            if (canteenId.equals(l.getCanteenId())) {
                bought += l.getPlatesBought();
                sold += l.getPlatesSold();
            }
        }
        return bought - sold;
    }
}
