package com.invex.employeeservice.service;

import com.invex.employeeservice.dto.EmployeeCreateRequest;
import com.invex.employeeservice.dto.EmployeeResponse;
import com.invex.employeeservice.dto.EmployeeUpdateRequest;
import com.invex.employeeservice.entity.Employee;
import com.invex.employeeservice.exception.EmployeeNotFoundException;
import com.invex.employeeservice.exception.InvalidRequestException;
import com.invex.employeeservice.mapper.EmployeeMapper;
import com.invex.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Juan");
        employee.setMiddleName("Carlos");
        employee.setPaternalLastName("Macedo");
        employee.setMaternalLastName("Mora");
        employee.setGender("MALE");
        employee.setBirthDate(LocalDate.of(1995, 4, 15));
        employee.setPosition("Java Developer");
        employee.setActive(true);

        employeeResponse = new EmployeeResponse();
        employeeResponse.setId(1L);
        employeeResponse.setFirstName("Juan");
        employeeResponse.setMiddleName("Carlos");
        employeeResponse.setPaternalLastName("Macedo");
        employeeResponse.setMaternalLastName("Mora");
        employeeResponse.setAge(31);
        employeeResponse.setGender("MALE");
        employeeResponse.setBirthDate(LocalDate.of(1995, 4, 15));
        employeeResponse.setPosition("Java Developer");
        employeeResponse.setActive(true);
    }

    @Test
    void findById_shouldReturnEmployee_whenEmployeeExists() {

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(employeeMapper.toResponse(employee))
                .thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getFirstName());

        verify(employeeRepository).findById(1L);
        verify(employeeMapper).toResponse(employee);
    }

    @Test
    void findById_shouldThrowException_whenEmployeeDoesNotExist() {

        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        EmployeeNotFoundException exception =
                assertThrows(
                        EmployeeNotFoundException.class,
                        () -> employeeService.findById(999L)
                );

        assertEquals("Employee not found with id: 999", exception.getMessage());

        verify(employeeRepository).findById(999L);

        verify(employeeMapper, never())
                .toResponse(any(Employee.class));
    }

    @Test
    void findAll_shouldReturnEmployees() {

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Ana");

        EmployeeResponse response2 = new EmployeeResponse();
        response2.setId(2L);
        response2.setFirstName("Ana");

        when(employeeRepository.findAll())
                .thenReturn(Arrays.asList(employee, employee2));

        when(employeeMapper.toResponse(employee))
                .thenReturn(employeeResponse);

        when(employeeMapper.toResponse(employee2))
                .thenReturn(response2);

        List<EmployeeResponse> result =
                employeeService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Juan", result.get(0).getFirstName());
        assertEquals("Ana", result.get(1).getFirstName());

        verify(employeeRepository).findAll();

        verify(employeeMapper, times(2))
                .toResponse(any(Employee.class));
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEmployeesExist() {

        when(employeeRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<EmployeeResponse> result =
                employeeService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(employeeRepository).findAll();

        verify(employeeMapper, never())
                .toResponse(any(Employee.class));
    }

    @Test
    void createAll_shouldCreateEmployees() {

        EmployeeCreateRequest request =
                new EmployeeCreateRequest();

        request.setFirstName("Juan");
        request.setPaternalLastName("Macedo");
        request.setGender("MALE");
        request.setBirthDate(LocalDate.of(1995, 4, 15));
        request.setPosition("Java Developer");

        when(employeeMapper.toEntity(request))
                .thenReturn(employee);

        when(employeeRepository.saveAll(Collections.singletonList(employee)))
                .thenReturn(Collections.singletonList(employee));

        when(employeeMapper.toResponse(employee))
                .thenReturn(employeeResponse);

        List<EmployeeResponse> result =
                employeeService.createAll(
                        Collections.singletonList(request)
                );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(employeeMapper).toEntity(request);

        verify(employeeRepository)
                .saveAll(Collections.singletonList(employee));

        verify(employeeMapper).toResponse(employee);
    }

    @Test
    void createAll_shouldThrowException_whenRequestListIsEmpty() {

        InvalidRequestException exception =
                assertThrows(
                        InvalidRequestException.class,
                        () -> employeeService.createAll(Collections.emptyList())
                );

        assertEquals("At least one employee is required", exception.getMessage());

        verify(employeeRepository, never()).saveAll(anyList());
    }

    @Test
    void update_shouldUpdateEmployee_whenEmployeeExists() {

        EmployeeUpdateRequest request =
                new EmployeeUpdateRequest();

        request.setPosition("Senior Java Developer");

        employee.setPosition("Senior Java Developer");

        employeeResponse.setPosition("Senior Java Developer");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        when(employeeMapper.toResponse(employee))
                .thenReturn(employeeResponse);

        EmployeeResponse result =
                employeeService.update(1L, request);

        assertNotNull(result);

        assertEquals("Senior Java Developer", result.getPosition());

        verify(employeeRepository).findById(1L);

        verify(employeeMapper)
                .updateEntity(request, employee);

        verify(employeeRepository).save(employee);

        verify(employeeMapper).toResponse(employee);
    }

    @Test
    void update_shouldThrowException_whenNoFieldsAreProvided() {

        EmployeeUpdateRequest request = new EmployeeUpdateRequest();

        InvalidRequestException exception =
                assertThrows(
                        InvalidRequestException.class,
                        () -> employeeService.update(1L, request)
                );

        assertEquals("At least one field must be provided for update", exception.getMessage());

        verify(employeeRepository, never()).findById(anyLong());

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void delete_shouldDeleteEmployee_whenEmployeeExists() {

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.delete(1L);

        verify(employeeRepository).findById(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void delete_shouldThrowException_whenEmployeeDoesNotExist() {

        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.delete(999L)
        );

        verify(employeeRepository).findById(999L);

        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void searchByName_shouldReturnMatchingEmployees() {

        when(employeeRepository.searchByName("Juan")).thenReturn(Collections.singletonList(employee));

        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        List<EmployeeResponse> result = employeeService.searchByName("   Juan   ");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstName());

        verify(employeeRepository).searchByName("Juan");

        verify(employeeMapper).toResponse(employee);
    }

    @Test
    void searchByName_shouldThrowException_whenNameIsBlank() {

        InvalidRequestException exception =
                assertThrows(
                        InvalidRequestException.class,
                        () -> employeeService.searchByName("   ")
                );

        assertEquals("Search name must not be blank", exception.getMessage());

        verify(employeeRepository, never()).searchByName(anyString());
    }
}