package org.example.tlonlineedupbackend.auth.service.Impl;

import org.example.tlonlineedupbackend.auth.entity.School;
import org.example.tlonlineedupbackend.auth.repository.SchoolRepository;
import org.example.tlonlineedupbackend.auth.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public Page<School> searchSchools(String keyword, Pageable pageable) {
        return schoolRepository.findByNameContainingOrSchoolCodeContaining(
                keyword,
                keyword,
                pageable
        );
    }

}
