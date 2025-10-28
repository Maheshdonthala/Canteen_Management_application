package com.jsp.canteen_management_system.service;

import com.jsp.canteen_management_system.dto.DashboardStats;
import com.jsp.canteen_management_system.model.FoodLog;
import com.jsp.canteen_management_system.repository.AttendanceRepository;
import com.jsp.canteen_management_system.repository.FoodLogRepository;
import com.jsp.canteen_management_system.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private FoodLogRepository foodLogRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        stats.setTotalSalesToday(calculateTodaySales());
        stats.setWorkersPresent(countWorkersPresentToday());
        stats.setSalariesPending(countPendingSalaries());

        return stats;
    }

    private double calculateTodaySales() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<FoodLog> logs = foodLogRepository.findByTimestampBetween(start, end);
        double total = 0.0;
        for (FoodLog log : logs) {
            total += log.getPlatesSold() * log.getPricePerPlate();
        }
        return total;
    }

    private long countWorkersPresentToday() {
        LocalDate today = LocalDate.now();
        return attendanceRepository.countByDateAndStatus(today, "PRESENT");
    }

    private long countPendingSalaries() {
        return salaryRepository.countByStatus("PENDING");
    }
}
