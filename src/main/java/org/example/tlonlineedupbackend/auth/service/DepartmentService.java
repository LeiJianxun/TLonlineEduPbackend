package org.example.tlonlineedupbackend.auth.service;

import org.example.tlonlineedupbackend.auth.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Optional;

public interface DepartmentService {
    Department addDepartment(Department department);
    void deleteDepartment(Long id);
    void deleteBatchDepartment(ArrayList<Long> ids);
    Optional<Department> getDepartment(Long id);
    Page<Department> getAllDepartment(Pageable pageable);
    Department updateDepartment(Long id, Department updatedDepartment);
    Page<Department> getDepartmentsWithNoParent(Pageable pageable);
    Page<Department> findByParentDCode(String parentDCode, Pageable pageable);
}
