package com.invex.employeeservice.mapper;

import com.invex.employeeservice.dto.EmployeeCreateRequest;
import com.invex.employeeservice.dto.EmployeeResponse;
import com.invex.employeeservice.dto.EmployeeUpdateRequest;
import com.invex.employeeservice.entity.Employee;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeCreateRequest request) {

        Employee employee = new Employee();

        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setPaternalLastName(request.getPaternalLastName());
        employee.setMaternalLastName(request.getMaternalLastName());
        employee.setGender(request.getGender());
        employee.setBirthDate(request.getBirthDate());
        employee.setPosition(request.getPosition());

        employee.setActive(request.getActive() != null ? request.getActive() : Boolean.TRUE);

        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {

        EmployeeResponse response = new EmployeeResponse();

        response.setId(employee.getId());
        response.setFirstName(employee.getFirstName());
        response.setMiddleName(employee.getMiddleName());
        response.setPaternalLastName(employee.getPaternalLastName());
        response.setMaternalLastName(employee.getMaternalLastName());
        response.setAge(calculateAge(employee.getBirthDate()));
        response.setGender(employee.getGender());
        response.setBirthDate(employee.getBirthDate());
        response.setPosition(employee.getPosition());
        response.setCreatedAt(employee.getCreatedAt());
        response.setActive(employee.getActive());

        return response;
    }

    public void updateEntity(EmployeeUpdateRequest request, Employee employee) {

        if (request.getFirstName() != null) {
            employee.setFirstName(request.getFirstName());
        }

        if (request.getMiddleName() != null) {
            employee.setMiddleName(request.getMiddleName());
        }

        if (request.getPaternalLastName() != null) {
            employee.setPaternalLastName(request.getPaternalLastName());
        }

        if (request.getMaternalLastName() != null) {
            employee.setMaternalLastName(request.getMaternalLastName());
        }

        if (request.getGender() != null) {
            employee.setGender(request.getGender());
        }

        if (request.getBirthDate() != null) {
            employee.setBirthDate(request.getBirthDate());
        }

        if (request.getPosition() != null) {
            employee.setPosition(request.getPosition());
        }

        if (request.getActive() != null) {
            employee.setActive(request.getActive());
        }
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}