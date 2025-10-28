package com.jsp.canteen_management_system.service;

import com.jsp.canteen_management_system.model.Attendance;
import com.jsp.canteen_management_system.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public Attendance markAttendance(String workerId, String status) {
        Attendance attendance = new Attendance();
        attendance.setWorkerId(workerId);
        attendance.setStatus(status);
        attendance.setDate(LocalDate.now()); // Sets the current date automatically

        return attendanceRepository.save(attendance);
    }
}
