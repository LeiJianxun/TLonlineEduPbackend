package org.example.tlonlineedupbackend.auth.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.tlonlineedupbackend.auth.entity.Department;
import org.example.tlonlineedupbackend.auth.repository.DepartmentRepository;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.example.tlonlineedupbackend.auth.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Department addDepartment(Department department) {
        // 从 session 获取当前用户
        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("user");  // 获取存储在 session 中的用户信息
        department.setCreate_user(userDetails.getId());
        department.setCreate_time(new Date());
        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    @Override
    public void deleteBatchDepartment(ArrayList<Long> ids) {
        for (Long id : ids) {
            departmentRepository.deleteById(id);
        }
    }

    @Override
    public Optional<Department> getDepartment(Long id) {
        return departmentRepository.findById(id);
    }

    @Override
    public Page<Department> getAllDepartment(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    @Override
    public Department updateDepartment(Long id, Department updatedDepartment) {
        // 1. 根据 id 查找要更新的部门
        Optional<Department> existingDepartmentOpt = departmentRepository.findById(id);
        if (existingDepartmentOpt.isEmpty()) {
            throw new RuntimeException("部门不存在，ID: " + id); // 如果部门不存在，抛出异常
        }

        // 2. 获取已存在的部门对象
        Department existingDepartment = existingDepartmentOpt.get();

        // 3. 更新部门字段
        if (updatedDepartment.getName() != null) {
            existingDepartment.setName(updatedDepartment.getName()); // 更新部门名称
        }

        // 4. 设置更新时间和更新用户
        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("user");
        existingDepartment.setUpdate_user(userDetails.getId()); // 设置更新用户
        existingDepartment.setUpdate_time(new Date()); // 设置更新时间

        // 5. 保存更新后的部门
        return departmentRepository.save(existingDepartment);
    }

    @Override
    public Page<Department> getDepartmentsWithNoParent(Pageable pageable) {
        // 查询parent_id为null的部门，即没有父部门的主部门
        return departmentRepository.findByParentIdIsNull(pageable);
    }

    @Override
    public Page<Department> findByParentDCode(String parentDCode, Pageable pageable) {
        // 添加下划线的模糊匹配（例如：父 d_code=01，查询 01_ 开头的子部门）
        return departmentRepository.findByDCodeStartingWith(parentDCode + "_", pageable);
    }
}
