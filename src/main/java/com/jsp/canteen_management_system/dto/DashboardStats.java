package com.jsp.canteen_management_system.dto;

import lombok.Data;

@Data
public class DashboardStats {
    private double totalSalesToday;
    private long workersPresent;
    private long salariesPending;
}
