package org.example.tlonlineedupbackend.auth.controller.LoginController;

import org.example.tlonlineedupbackend.auth.entity.Department;
import org.example.tlonlineedupbackend.auth.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<Department> addDepartment(@RequestBody Department department) {
        Department createdDepartment = departmentService.addDepartment(department);

        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBatchDepartment(@RequestBody ArrayList<Long> ids) {
        departmentService.deleteBatchDepartment(ids); // 调用删除方法
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 返回无内容的响应
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartment(@PathVariable Long id) {
        Optional<Department> course = departmentService.getDepartment(id);
        return course.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Department>> getAllDepartment(Pageable pageable) {
        Page<Department> courses = departmentService.getAllDepartment(pageable);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/addChildren")
    public ResponseEntity<Page<Department>> getAllParentDepartments(Pageable pageable) {
        // 查询 parent_id 为 null 的部门，即主部门
        Page<Department> departments = departmentService.getDepartmentsWithNoParent(pageable);
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @GetMapping("/searchCode")
    public ResponseEntity<Page<Department>> searchByParentDCode(@RequestParam String parentDCode, Pageable pageable) {
        Page<Department> departments = departmentService.findByParentDCode(parentDCode, pageable);
        return ResponseEntity.ok(departments);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department updatedDepartment) {
        Department department = departmentService.updateDepartment(id, updatedDepartment);
        return ResponseEntity.ok(department);
    }
}
