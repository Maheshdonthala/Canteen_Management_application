package com.jsp.canteen_management_system.controller;

import com.jsp.canteen_management_system.model.FoodLog;
import com.jsp.canteen_management_system.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FoodController {

    @Autowired
    private FoodService foodService;

    // A simple DTO class to receive the request data
    static class SaleRequest {
        public int platesSold;
    }

    static class PurchaseRequest {
        public int platesBought;
    }

    @PostMapping("/canteens/{canteenId}/sales")
    public FoodLog createSaleLog(@PathVariable String canteenId, @RequestBody SaleRequest request) {
        return foodService.recordSale(canteenId, request.platesSold);
    }

    @PostMapping("/canteens/{canteenId}/purchases")
    public FoodLog createPurchaseLog(@PathVariable String canteenId, @RequestBody PurchaseRequest request) {
        return foodService.recordPurchase(canteenId, request.platesBought);
    }

    @GetMapping("/canteens/{canteenId}/inventory")
    public Integer getRemainingPlates(@PathVariable String canteenId) {
        return foodService.getRemainingPlatesToday(canteenId);
    }
}
