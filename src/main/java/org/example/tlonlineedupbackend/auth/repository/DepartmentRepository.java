package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findById(Long id);
    // 根据部门代码查询部门
    Optional<Department> findByDCode(String dcode);
    // 根据父部门ID查询子部门
    List<Department> findByParentIdAndParentIdNotNull(Long parentId);
    Page<Department> findByParentIdIsNull(Pageable pageable);
    Page<Department> findByDCodeStartingWith(String parentDCode, Pageable pageable);

    // 根据父部门ID和关键字查询子部门
    List<Department> findByParentIdAndNameContaining(
            Long parentId, String nameKeyword
    );
}
