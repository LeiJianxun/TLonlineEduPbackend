package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.User;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByPhone(String phone);
    Optional<User> findById(String id);
    // 根据部门ID列表查询用户
    List<User> findByAscriptionIn(List<Long> departmentIds);
}
