package com.jsp.canteen_management_system.controller;

import com.jsp.canteen_management_system.model.Worker;
import com.jsp.canteen_management_system.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    @Autowired
    private WorkerRepository workerRepository;

    @GetMapping
    public List<Worker> list() {
        return workerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Worker get(@PathVariable String id) {
        return workerRepository.findById(id).orElseThrow(() -> new RuntimeException("Worker not found"));
    }

    @PostMapping
    public Worker create(@RequestBody Worker worker) {
        // ensure id is null so Mongo will create one
        worker.setId(null);
        return workerRepository.save(worker);
    }

    @PutMapping("/{id}")
    public Worker update(@PathVariable String id, @RequestBody Worker payload) {
        Worker w = workerRepository.findById(id).orElseThrow(() -> new RuntimeException("Worker not found"));
        w.setName(payload.getName());
        w.setRole(payload.getRole());
        return workerRepository.save(w);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        workerRepository.deleteById(id);
    }
}
