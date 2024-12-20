package com.portfolio.server.employees.service;

import com.portfolio.server.common.exception.CustomException;
import com.portfolio.server.common.exception.ExceptionCode;
import com.portfolio.server.employees.dto.EmployeesDto;
import com.portfolio.server.employees.entity.Employees;
import com.portfolio.server.employees.repo.EmployeesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetEmployeeService {

    private final EmployeesRepository employeesRepository;

    @Transactional(readOnly = true)
    public List<EmployeesDto.EmployeesSummary> executeForList(){
        return employeesRepository.findAll().stream().map(
                employees -> EmployeesDto.EmployeesSummary.builder()
                        .employeeId(employees.getEmployeeId())
                        .firstName(employees.getFirstName())
                        .lastName(employees.getLastName())
                        .email(employees.getEmail())
                        .phoneNumber(employees.getPhoneNumber())
                        .hireDate(employees.getHireDate())
                        .jobId(employees.getJobs().getJobId())
                        .salary(employees.getSalary())
                        .commissionPct(employees.getCommissionPct())
                        .managerId(employees.getManager() != null ? employees.getManager().getEmployeeId() : null)
                        .departmentId(employees.getDepartments() != null ? employees.getDepartments().getDepartmentId() : null)
                        .build()
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeesDto.EmployeesSummary execute(int employeeId){
        Employees employees = employeesRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException(ExceptionCode.EMPLOYEES_NOT_FOUND));
        return EmployeesDto.EmployeesSummary.builder()
                .employeeId(employees.getEmployeeId())
                .firstName(employees.getFirstName())
                .lastName(employees.getLastName())
                .email(employees.getEmail())
                .phoneNumber(employees.getPhoneNumber())
                .hireDate(employees.getHireDate())
                .jobId(employees.getJobs().getJobId())
                .salary(employees.getSalary())
                .commissionPct(employees.getCommissionPct())
                .managerId(employees.getManager() != null ? employees.getManager().getEmployeeId() : null)
                .departmentId(employees.getDepartments() != null ? employees.getDepartments().getDepartmentId() : null)
                .build();
    }
}
