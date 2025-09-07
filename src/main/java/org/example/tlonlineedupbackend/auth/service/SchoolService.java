package org.example.tlonlineedupbackend.auth.service;

import org.example.tlonlineedupbackend.auth.entity.Department;
import org.example.tlonlineedupbackend.auth.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Optional;

public interface SchoolService {
    Page<School> searchSchools(String keyword, Pageable pageable);
}
