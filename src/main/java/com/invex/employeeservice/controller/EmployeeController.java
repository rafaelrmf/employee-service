package com.invex.employeeservice.controller;

import com.invex.employeeservice.dto.EmployeeCreateRequest;
import com.invex.employeeservice.dto.EmployeeResponse;
import com.invex.employeeservice.dto.EmployeeUpdateRequest;
import com.invex.employeeservice.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAll() {

        List<EmployeeResponse> employees = employeeService.findAll();

        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(@PathVariable Long id) {

        EmployeeResponse employee = employeeService.findById(id);

        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<List<EmployeeResponse>> create(
            @Valid @RequestBody List<@Valid EmployeeCreateRequest> requests) {

        List<EmployeeResponse> employees = employeeService.createAll(requests);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest request) {

        EmployeeResponse employee = employeeService.update(id, request);

        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        employeeService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponse>> searchByName(
            @RequestParam String name) {

        List<EmployeeResponse> employees = employeeService.searchByName(name);

        return ResponseEntity.ok(employees);
    }
}