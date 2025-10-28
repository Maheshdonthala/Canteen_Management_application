package com.jsp.canteen_management_system.controller;

import com.jsp.canteen_management_system.model.Salary;
import com.jsp.canteen_management_system.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    @Autowired
    private SalaryRepository salaryRepository;

    @GetMapping("/status/{status}")
    public List<Salary> listByStatus(@PathVariable String status) {
        return salaryRepository.findAll().stream().filter(s -> status.equalsIgnoreCase(s.getStatus())).toList();
    }

    @PostMapping("/{id}/mark")
    public Salary markSalary(@PathVariable String id, @RequestBody String status) {
        Salary s = salaryRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary not found"));
        s.setStatus(status);
        return salaryRepository.save(s);
    }
}
