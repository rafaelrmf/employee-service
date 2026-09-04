package com.invex.employeeservice.repository;

import com.invex.employeeservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e " +
            "WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(COALESCE(e.middleName, '')) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(e.paternalLastName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(COALESCE(e.maternalLastName, '')) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> searchByName(@Param("name") String name);
}