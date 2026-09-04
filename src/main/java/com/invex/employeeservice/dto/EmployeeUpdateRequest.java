package com.invex.employeeservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class EmployeeUpdateRequest {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "First name must not be blank")
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "Middle name must not be blank")
    private String middleName;

    @Size(max = 100, message = "Paternal last name must not exceed 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "Paternal last name must not be blank")
    private String paternalLastName;

    @Size(max = 100, message = "Maternal last name must not exceed 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "Maternal last name must not be blank")
    private String maternalLastName;

    @Size(max = 20, message = "Gender must not exceed 20 characters")
    @Pattern(regexp = ".*\\S.*", message = "Gender must not be blank")
    private String gender;

    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;

    @Size(max = 100, message = "Position must not exceed 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "Position must not be blank")
    private String position;

    private Boolean active;

    public EmployeeUpdateRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPaternalLastName() {
        return paternalLastName;
    }

    public void setPaternalLastName(String paternalLastName) {
        this.paternalLastName = paternalLastName;
    }

    public String getMaternalLastName() {
        return maternalLastName;
    }

    public void setMaternalLastName(String maternalLastName) {
        this.maternalLastName = maternalLastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}