package org.example.tlonlineedupbackend.auth.util;

import org.example.tlonlineedupbackend.auth.entity.Department;
import org.example.tlonlineedupbackend.auth.repository.DepartmentRepository;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

@Component
public class StringToDepartmentConverter implements Converter<String, Department> {
    private final DepartmentRepository departmentRepository;

    public StringToDepartmentConverter(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department convert(String source) {
        Long departmentId = Long.parseLong(source);
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("无效的部门ID: " + departmentId));
    }
}
