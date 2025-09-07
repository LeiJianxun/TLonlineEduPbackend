package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.Department;
import org.example.tlonlineedupbackend.auth.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findById(Long id);
    Page<School> findByNameContainingOrSchoolCodeContaining(
            String nameKeyword,
            String schoolCodeKeyword,
            Pageable pageable
    );
}
