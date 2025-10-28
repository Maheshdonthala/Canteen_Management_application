package com.jsp.canteen_management_system.service;

import com.jsp.canteen_management_system.model.Canteen;
import com.jsp.canteen_management_system.repository.CanteenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanteenService {

    @Autowired
    private CanteenRepository canteenRepository;

    // Method to get all canteens
    public List<Canteen> getAllCanteens() {
        return canteenRepository.findAll();
    }

    // Method to add a new canteen
    public Canteen addCanteen(Canteen canteen) {
        return canteenRepository.save(canteen);
    }

    // Method to delete a canteen by id
    public void deleteCanteen(String id) {
        canteenRepository.deleteById(id);
    }
}
