package com.invex.employeeservice.service;

import com.invex.employeeservice.dto.EmployeeCreateRequest;
import com.invex.employeeservice.dto.EmployeeResponse;
import com.invex.employeeservice.dto.EmployeeUpdateRequest;

import java.util.List;

public interface EmployeeService {

    List<EmployeeResponse> findAll();

    EmployeeResponse findById(Long id);

    EmployeeResponse create(EmployeeCreateRequest request);

    List<EmployeeResponse> createAll(List<EmployeeCreateRequest> requests);

    EmployeeResponse update(Long id, EmployeeUpdateRequest request);

    void delete(Long id);

    List<EmployeeResponse> searchByName(String name);
}