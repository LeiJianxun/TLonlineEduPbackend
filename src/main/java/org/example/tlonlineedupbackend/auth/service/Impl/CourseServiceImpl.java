package org.example.tlonlineedupbackend.auth.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.tlonlineedupbackend.auth.entity.*;
import org.example.tlonlineedupbackend.auth.repository.*;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.example.tlonlineedupbackend.auth.service.CourseService;
import org.example.tlonlineedupbackend.auth.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseDepartmentRepository courseDepartmentRepository;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private CourseTaskRepository courseTaskRepository;
    @Autowired
    private OfflineScheduleRepository offlineScheduleRepository;
    @Autowired
    private StudentTaskRepository studentTaskRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Override
    public Course addCourse(Course course) {
        // 从 session 获取当前用户
        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("user");  // 获取存储在 session 中的用户信息
        course.setCreate_user(userDetails.getId());
        course.setCreate_time(new Date());
        course.setAscription(userDetails.getAscription());
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        Optional<Course> course = getCourse(id);
        // 删除课程归属
        List<CourseDepartment> byCourse = courseDepartmentRepository.findByCourse(course.get());
        for (CourseDepartment courseDepartment : byCourse) {
            courseDepartmentRepository.delete(courseDepartment);
        }
        // 删除课程下所有任务
        List<CourseTask> byCourseTask = courseTaskRepository.findByCourse(course.get());
        for (CourseTask courseTask : byCourseTask) {
            // 删除线下课程任务
            List<OfflineSchedule> byTaskId = offlineScheduleRepository.findByCourseTask(courseTask);
            offlineScheduleRepository.deleteAll(byTaskId);
            // 删除相关学生任务
            List<StudentTask> byTaskIdStudent = studentTaskRepository.findByTask(courseTask);
            studentTaskRepository.deleteAll(byTaskIdStudent);
            //删除课程包含的的附件
            List<Attachment> byCourseTaskAttachment = attachmentRepository.findByCourseTask(courseTask);
            attachmentRepository.deleteAll(byCourseTaskAttachment);

            courseTaskRepository.delete(courseTask);
        }

        courseRepository.deleteById(id);
    }

    @Override
    public Optional<Course> getCourse(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public List<Course> getAscriptionCourses(Long ascription) {
        return courseRepository.findByAscription(ascription);
    }

    @Override
    public void assignCourseToDepartment(Long courseId, Long departmentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 防止重复分配
        if (!courseDepartmentRepository.existsByCourseIdAndDepartmentId(courseId, departmentId)) {
            CourseDepartment cd = new CourseDepartment();
            cd.setCourse(course);
            cd.setDepartmentId(departmentId);
            courseDepartmentRepository.save(cd);
        }
    }

    @Override
    public List<Course> getCoursesByDepartment(Long departmentId) {
        return courseDepartmentRepository.findByDepartmentId(departmentId)
                .stream()
                .map(CourseDepartment::getCourse)
                .collect(Collectors.toList());
    }

    // 获取在线人数前三的课程
    public List<Map<String, Object>> getHotCourses() {
        List<Course> allCourses = courseRepository.findAll();
        Map<Long, Long> onlineCounts = new HashMap<>();
        for (Course course : allCourses) {
            Long courseId = course.getId();
            Long onlineCount = redisUtil.getValue("course:" + courseId);
            if (onlineCount == null) {
                onlineCount = 0L;
            }
            onlineCounts.put(courseId, onlineCount);
        }

        List<Map.Entry<Long, Long>> list = new ArrayList<>(onlineCounts.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<Map<String, Object>> hotCourses = new ArrayList<>();
        for (int i = 0; i < Math.min(3, list.size()); i++) {
            Long courseId = list.get(i).getKey();
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course != null && list.get(i).getValue() != 0) {
                Map<String, Object> courseInfo = new HashMap<>();
                courseInfo.put("course", course);
                courseInfo.put("onlineCount", list.get(i).getValue());
                hotCourses.add(courseInfo);
            }
        }
        return hotCourses;
    }

    // 用户进入课程页面，在线人数加 1
    public void enterCourse(Long courseId) {
        redisUtil.increment("course:" + courseId);
    }

    // 用户离开课程页面，在线人数减 1
    public void leaveCourse(Long courseId) {
        redisUtil.decrement("course:" + courseId);
    }

    @Override
    public List<Course> searchCourse(String keyword) {
        List<Course> likeName = courseRepository.findByNameContaining(keyword);
        if (likeName != null) {
            return likeName;
        }
        List<Course> byB1Containing = courseRepository.findByB1Containing(keyword);
        if (byB1Containing != null) {
            return byB1Containing;
        }
        List<Course> byB2Containing = courseRepository.findByB2Containing(keyword);
        if (byB2Containing != null) {
            return byB2Containing;
        }

        return null;
    }

}
