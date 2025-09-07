package org.example.tlonlineedupbackend.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.tlonlineedupbackend.auth.entity.Course;
import org.example.tlonlineedupbackend.auth.entity.CourseDepartment;
import org.example.tlonlineedupbackend.auth.repository.CourseDepartmentRepository;
import org.example.tlonlineedupbackend.auth.repository.CourseRepository;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.example.tlonlineedupbackend.auth.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseDepartmentRepository courseDepartmentRepository;

    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        Course createdCourse = courseService.addCourse(course);

        Course newCourse = courseRepository.findById(createdCourse.getId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 防止重复分配
        if (!courseDepartmentRepository.existsByCourseIdAndDepartmentId(createdCourse.getId(), createdCourse.getAscription())) {
            CourseDepartment cd = new CourseDepartment();
            cd.setCourse(newCourse);
            cd.setDepartmentId(createdCourse.getAscription());
            courseDepartmentRepository.save(cd);
        }

        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @GetMapping("/searchCourse/{keyword}")
    public ResponseEntity<List<Course>> searchCourse(HttpServletRequest request ,@PathVariable String keyword){
        // 从 session 获取当前用户
        HttpSession session = request.getSession();
        CustomUserDetails currentUser = (CustomUserDetails) session.getAttribute("user");
        // 登录用户检测
        if (currentUser != null){
            // 管理员可查找所有课程
            if (Objects.equals(currentUser.getIdentifiers(), "Root")){
                List<Course> courses = courseService.searchCourse(keyword);
                return new ResponseEntity<>(courses, HttpStatus.OK);
            }else {
                // 非管理员只可查找所属课程
                List<CourseDepartment> byDepartmentId = courseDepartmentRepository.findByDepartmentId(currentUser.getAscription());
                List<Course> courses = new java.util.ArrayList<>(List.of());
                for (CourseDepartment course : byDepartmentId){
                    if (course.getCourse().getName().contains(keyword) || course.getCourse().getB1().contains(keyword) || course.getCourse().getB2().contains(keyword)){
                        Course course1 = course.getCourse();
                        courses.add(course1);
                    }
                }

                return new ResponseEntity<>(courses, HttpStatus.OK);
            }
        }else {
            // 未登录用户只可查找公用课程
            List<Course> courses = courseService.searchCourse(keyword).stream().filter(course -> course.getAscription() == 1L).toList();
            return new ResponseEntity<>(courses, HttpStatus.OK);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourse(id);
        return course.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Course>> getAllCourses(Pageable pageable) {
        Page<Course> courses = courseService.getAllCourses(pageable);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/getByAscription")
    public ResponseEntity<List<Course>> getByAscription(HttpServletRequest request) {
        // 从 session 获取当前用户
        HttpSession session = request.getSession();
        CustomUserDetails currentUser = (CustomUserDetails) session.getAttribute("user");
        Long ascription = currentUser.getAscription();
        List<Course> courses = courseService.getAscriptionCourses(ascription);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @PostMapping("/{courseId}/assign")
    public ResponseEntity<Void> assignCourseToDepartment(
            @PathVariable Long courseId,
            @RequestParam Long departmentId
    ) {
        courseService.assignCourseToDepartment(courseId, departmentId);
        return ResponseEntity.ok().build();
    }

    // 根据部门获取课程
    @GetMapping("/byDepartment")
    public ResponseEntity<List<Course>> getCoursesByDepartment(
            @RequestParam Long departmentId
    ) {
        return ResponseEntity.ok(courseService.getCoursesByDepartment(departmentId));
    }

    // 获取在线人数前三的课程
    @GetMapping("/hot-courses")
    public List<Map<String, Object>> getHotCourses() {
        return courseService.getHotCourses();
    }

    // 用户进入课程页面，在线人数加 1
    @PostMapping("/{courseId}/enter")
    public void enterCourse(@PathVariable Long courseId) {
        courseService.enterCourse(courseId);
    }

    // 用户离开课程页面，在线人数减 1
    @PostMapping("/{courseId}/leave")
    public void leaveCourse(@PathVariable Long courseId) {
        courseService.leaveCourse(courseId);
    }
}
