package org.example.tlonlineedupbackend.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.tlonlineedupbackend.auth.entity.Course;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.example.tlonlineedupbackend.auth.service.AllUserService;
import org.example.tlonlineedupbackend.auth.service.CourseService;
import org.example.tlonlineedupbackend.auth.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/start")
public class StartController {

    @Autowired
    private AllUserService allUserService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private RedisUtil redisUtil;

    private static final String ONLINE_USERS_KEY = "online_users";

    @GetMapping("/getLoginData")
    public ResponseEntity<?> getLoginData(HttpServletRequest request){
        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("user");
        if(userDetails == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        }
    }


    @GetMapping("/getUserCount")
    public ResponseEntity<?> getUserCount(Pageable pageable) {
        Page<User> allUser = allUserService.getAllUser(pageable);
        int size = allUser.getContent().size();
        return new ResponseEntity<>(size, HttpStatus.OK);
    }

    @GetMapping("/getCourseCount")
    public ResponseEntity<?> getCourseCount(Pageable pageable) {
        Page<Course> allCourses = courseService.getAllCourses(pageable);
        int size = allCourses.getContent().size();
        return new ResponseEntity<>(size, HttpStatus.OK);
    }

    @GetMapping("/getCourseTypeCount")
    public ResponseEntity<?> getCourseTypeCount(Pageable pageable) {
        Map<String, Integer> response = new HashMap<>();
        Page<Course> allCourses = courseService.getAllCourses(pageable);

        for (Course course : allCourses.getContent()) {
            String courseType = course.getCourse_type();

            // 如果课程类型已经存在于 Map 中，则累加计数
            if (response.containsKey(courseType)) {
                response.put(courseType, response.get(courseType) + 1);
            } else {
                // 如果课程类型不存在于 Map 中，则初始化为 1
                response.put(courseType, 1);
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/heartbeat")
    public void heartbeat() {
        redisUtil.incrementUser(ONLINE_USERS_KEY);
    }

    @PostMapping("/heartbeatUser")
    public ResponseEntity<?> heartbeat(@RequestHeader("Authorization") String userId) {
        if (userId != null) {
            redisUtil.addOnlineUser(userId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/leave")
    public void leave() {
        redisUtil.decrementUser(ONLINE_USERS_KEY);
    }

    @GetMapping("/count")
    public Long getOnlineUserCount() {
        if (redisUtil.getOnlineCount() != null){
            return redisUtil.getValueUser(ONLINE_USERS_KEY) + redisUtil.getOnlineCount();
        }
        return redisUtil.getValueUser(ONLINE_USERS_KEY);
    }

}
