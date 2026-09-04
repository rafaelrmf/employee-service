package com.invex.employeeservice.service;

import com.invex.employeeservice.dto.EmployeeCreateRequest;
import com.invex.employeeservice.dto.EmployeeResponse;
import com.invex.employeeservice.dto.EmployeeUpdateRequest;
import com.invex.employeeservice.entity.Employee;
import com.invex.employeeservice.exception.EmployeeNotFoundException;
import com.invex.employeeservice.exception.InvalidRequestException;
import com.invex.employeeservice.mapper.EmployeeMapper;
import com.invex.employeeservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse findById(Long id) {
        Employee employee = findEmployeeById(id);
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeCreateRequest request) {

        Employee employee = employeeMapper.toEntity(request);

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional
    public List<EmployeeResponse> createAll(List<EmployeeCreateRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new InvalidRequestException(
                    "At least one employee is required"
            );
        }

        requests.forEach(request ->
                validateBirthDate(request.getBirthDate())
        );

        List<Employee> employees = requests.stream()
                .map(employeeMapper::toEntity)
                .collect(Collectors.toList());

        return employeeRepository.saveAll(employees)
                .stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validateBirthDate(LocalDate birthDate) {

        if (birthDate != null && !birthDate.isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Birth date must be in the past");
        }
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {

        validateUpdateRequest(request);

        Employee employee = findEmployeeById(id);

        employeeMapper.updateEntity(request, employee);

        Employee updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Employee employee = findEmployeeById(id);

        employeeRepository.delete(employee);
    }

    @Override
    public List<EmployeeResponse> searchByName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRequestException("Search name must not be blank");
        }

        String normalizedName = name.trim();

        if (normalizedName.length() > 100) {
            throw new InvalidRequestException("Search name must not exceed 100 characters");}

        return employeeRepository.searchByName(normalizedName)
                .stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validateUpdateRequest(EmployeeUpdateRequest request) {

        boolean noFieldsProvided =
                request.getFirstName() == null
                        && request.getMiddleName() == null
                        && request.getPaternalLastName() == null
                        && request.getMaternalLastName() == null
                        && request.getGender() == null
                        && request.getBirthDate() == null
                        && request.getPosition() == null
                        && request.getActive() == null;

        if (noFieldsProvided) {
            throw new InvalidRequestException("At least one field must be provided for update");
        }
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}