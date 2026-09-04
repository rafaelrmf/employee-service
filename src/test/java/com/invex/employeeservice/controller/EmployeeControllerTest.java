package com.invex.employeeservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invex.employeeservice.dto.EmployeeCreateRequest;
import com.invex.employeeservice.dto.EmployeeResponse;
import com.invex.employeeservice.dto.EmployeeUpdateRequest;
import com.invex.employeeservice.exception.EmployeeNotFoundException;
import com.invex.employeeservice.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {

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
        employeeResponse.setCreatedAt(
                LocalDateTime.of(2026, 9, 3, 22, 30, 0)
        );
        employeeResponse.setActive(true);
    }

    @Test
    void findAll_shouldReturnOkAndEmployees() throws Exception {

        when(employeeService.findAll())
                .thenReturn(Collections.singletonList(employeeResponse));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Juan"))
                .andExpect(jsonPath("$[0].age").value(31))
                .andExpect(jsonPath("$[0].position").value("Java Developer"))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(employeeService).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoEmployeesExist() throws Exception {

        when(employeeService.findAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        verify(employeeService).findAll();
    }

    @Test
    void findById_shouldReturnEmployee_whenEmployeeExists() throws Exception {

        when(employeeService.findById(1L))
                .thenReturn(employeeResponse);

        mockMvc.perform(get("/employees/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.paternalLastName").value("Macedo"))
                .andExpect(jsonPath("$.birthDate").value("15-04-1995"));

        verify(employeeService).findById(1L);
    }

    @Test
    void findById_shouldReturnNotFound_whenEmployeeDoesNotExist()
            throws Exception {

        when(employeeService.findById(999L))
                .thenThrow(new EmployeeNotFoundException(999L));

        mockMvc.perform(get("/employees/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/employees/999"));

        verify(employeeService).findById(999L);
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdIsInvalid()
            throws Exception {

        mockMvc.perform(get("/employees/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value for parameter: id"));

        verify(employeeService, never()).findById(anyLong());
    }

    @Test
    void create_shouldReturnCreated_whenRequestIsValid()
            throws Exception {

        EmployeeCreateRequest request = new EmployeeCreateRequest();

        request.setFirstName("Juan");
        request.setMiddleName("Carlos");
        request.setPaternalLastName("Macedo");
        request.setMaternalLastName("Mora");
        request.setGender("MALE");
        request.setBirthDate(LocalDate.of(1995, 4, 15));
        request.setPosition("Java Developer");
        request.setActive(true);

        when(employeeService.createAll(anyList()))
                .thenReturn(Collections.singletonList(employeeResponse));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(request))))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Juan"))
                .andExpect(jsonPath("$[0].age").value(31));

        verify(employeeService).createAll(anyList());
    }

    @Test
    void create_shouldReturnBadRequest_whenRequiredFieldsAreInvalid()
            throws Exception {

        String requestBody =
                "[{" +
                        "\"firstName\":\"\"," +
                        "\"paternalLastName\":\"\"," +
                        "\"gender\":\"\"," +
                        "\"birthDate\":\"20-08-2035\"," +
                        "\"position\":\"\"" +
                        "}]";

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)).andExpect(status().isBadRequest());

        verify(employeeService, never()).createAll(anyList());
    }

    @Test
    void create_shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {

        String invalidJson = "[{\"firstName\":\"Juan\"";

        mockMvc.perform(post("/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request or invalid field format"));

        verify(employeeService, never()).createAll(anyList());
    }

    @Test
    void update_shouldReturnOk_whenRequestIsValid()
            throws Exception {

        EmployeeUpdateRequest request = new EmployeeUpdateRequest();

        request.setPosition("Senior Java Developer");
        request.setActive(false);

        employeeResponse.setPosition("Senior Java Developer");
        employeeResponse.setActive(false);

        when(employeeService.update(
                eq(1L),
                any(EmployeeUpdateRequest.class)
        )).thenReturn(employeeResponse);

        mockMvc.perform(put("/employees/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("Senior Java Developer"))
                .andExpect(jsonPath("$.active").value(false));

        verify(employeeService).update(eq(1L), any(EmployeeUpdateRequest.class));
    }

    @Test
    void update_shouldReturnBadRequest_whenFieldIsBlank()
            throws Exception {

        String requestBody = "{\"position\":\"    \"}";

        mockMvc.perform(put("/employees/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"));

        verify(employeeService, never()).update(anyLong(), any(EmployeeUpdateRequest.class));
    }

    @Test
    void delete_shouldReturnNoContent_whenEmployeeExists()
            throws Exception {

        doNothing().when(employeeService).delete(1L);

        mockMvc.perform(delete("/employees/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(employeeService).delete(1L);
    }

    @Test
    void delete_shouldReturnNotFound_whenEmployeeDoesNotExist()
            throws Exception {

        doThrow(new EmployeeNotFoundException(999L))
                .when(employeeService)
                .delete(999L);

        mockMvc.perform(delete("/employees/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 999"));

        verify(employeeService).delete(999L);
    }

    @Test
    void searchByName_shouldReturnMatchingEmployees()
            throws Exception {

        when(employeeService.searchByName("Juan")).thenReturn(Collections.singletonList(employeeResponse));

        mockMvc.perform(get("/employees/search").param("name", "Juan"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName").value("Juan"));

        verify(employeeService).searchByName("Juan");
    }

    @Test
    void searchByName_shouldReturnBadRequest_whenNameParameterIsMissing()
            throws Exception {

        mockMvc.perform(
                        get("/employees/search")
                )
                .andExpect(status().isBadRequest());

        verify(employeeService, never())
                .searchByName(anyString());
    }
}