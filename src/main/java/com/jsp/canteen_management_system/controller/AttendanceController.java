package com.jsp.canteen_management_system.controller;

import com.jsp.canteen_management_system.model.Attendance;
import com.jsp.canteen_management_system.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // Mark attendance for a worker (basic)
    static class AttendanceRequest {
        public String workerId;
        public String status; // PRESENT/ABSENT
    }

    @PostMapping
    public Attendance mark(@RequestBody AttendanceRequest req) {
        return attendanceService.markAttendance(req.workerId, req.status);
    }

}
