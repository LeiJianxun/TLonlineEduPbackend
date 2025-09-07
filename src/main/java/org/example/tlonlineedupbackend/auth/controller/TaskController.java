package org.example.tlonlineedupbackend.auth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tlonlineedupbackend.auth.entity.*;
import org.example.tlonlineedupbackend.auth.exception.FileStorageException;
import org.example.tlonlineedupbackend.auth.exception.ResourceNotFoundException;
import org.example.tlonlineedupbackend.auth.repository.*;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.example.tlonlineedupbackend.auth.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final CourseTaskRepository courseTaskRepository;
    private final AttachmentRepository attachmentRepository;
    @Autowired
    private StudentTaskRepository studentTaskRepository;
    @Autowired
    private OfflineScheduleRepository offlineScheduleRepository;

    @Autowired
    private exerciseQuestionRepository exerciseQuestionRepository;

    public TaskController(CourseTaskRepository courseTaskRepository, AttachmentRepository attachmentRepository, AttachmentService attachmentService, UserRepository userRepository) {
        this.courseTaskRepository = courseTaskRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
        this.userRepository = userRepository;
    }

    private final AttachmentService attachmentService;

    private final UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;
    // 发布任务接口
    @PostMapping("/pushTasks")
    public ResponseEntity<?> publishTask(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long did,
            @RequestParam CourseTask.TaskType taskType,
            @RequestParam("course") Course course,
            @RequestParam("department") Department department,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) List apTime,
            @RequestParam(required = false) String classRoom,
            @RequestParam(required = false) String exercises, // 新增：题目数据（JSON格式）
            @AuthenticationPrincipal User user
    ) {

        // 1. 保存任务
        HttpSession session = request.getSession();
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("user");
        User users = userDetails.toUser();
        CourseTask task = new CourseTask();
        task.setTaskType(taskType);
        task.setCourse(course);
        task.setDepartment(department);
        task.setTitle(title);
        task.setCreator(users);
        task.setContent(content);
        task.setEndTime(endTime);
        courseTaskRepository.save(task);

        if (taskType == CourseTask.TaskType.EXERCISE && exercises != null) {
            List<ExerciseQuestion> questionList = parseExercises(exercises, task);
            exerciseQuestionRepository.saveAll(questionList);
        }

        // 2. 保存附件
        // 处理逻辑时检查文件是否存在
//        if (files != null && !files.isEmpty()) {
////            files.forEach(file -> {
////                if (!file.isEmpty()) { // 跳过空占位文件
////                    Attachment attachment = attachmentService.uploadAttachment(task.getId(), file, user);
////                    attachmentRepository.save(attachment);
////                }
////            });
//        }

        // 3. 为学生创建任务
        List<User> byAscriptionIn = userRepository.findByAscriptionIn(Collections.singletonList(did));
        for (int i = 0; i < byAscriptionIn.size(); i++) {
            StudentTask studentTask = new StudentTask();
            studentTask.setStudent(byAscriptionIn.get(i));
            studentTask.setTask(task);
            studentTask.setStatus(StudentTask.Status.valueOf("NOT_STARTED"));
            studentTaskRepository.save(studentTask);
        }

        // 4.线下课程
        if (taskType == CourseTask.TaskType.OFFLINE){
            OfflineSchedule offlineSchedule = new OfflineSchedule();
            offlineSchedule.setCourseTask(task);
            StringBuilder sb = new StringBuilder();
            for (int i =0 ; i < apTime.size(); i++){
                if (i > 0) {
                    sb.append(";");
                }
                sb.append(apTime.get(i));
            }
            String stringTime = sb.toString();
            offlineSchedule.setScheduleTime(stringTime);
            offlineSchedule.setClassroom(classRoom);
            offlineSchedule.setDuration(45);
            offlineScheduleRepository.save(offlineSchedule);
        }

        return ResponseEntity.ok().build();
    }

    // 删除附件接口
    @DeleteMapping("/attachments/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("附件不存在"));

        // 删除文件
        try {
            Files.deleteIfExists(Paths.get("./upload", attachment.getStoredName()));
        } catch (IOException e) {
            throw new FileStorageException("文件删除失败");
        }

        // 删除数据库记录
        attachmentRepository.delete(attachment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{taskId}/questions")
    public ResponseEntity<?> getExerciseQuestions(@PathVariable Long taskId) {
        try {
            // 1. 查找任务
            CourseTask task = courseTaskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("任务不存在"));

            // 2. 获取题目列表
            List<ExerciseQuestion> questions = exerciseQuestionRepository.findByTask(task);

            // 3. 构建响应数据
            List<Map<String, Object>> response = questions.stream().map(q -> {
                Map<String, Object> questionMap = new HashMap<>();
                questionMap.put("id", q.getId());
                questionMap.put("type", q.getType().name());
                questionMap.put("question", q.getQuestion());
                questionMap.put("analysis", q.getAnalysis());

                // 处理选择题选项
                if (q.getType() != ExerciseQuestion.QuestionType.SUBJECTIVE) {
                    questionMap.put("options", q.getOptions().stream()
                            .map(opt -> Map.of(
                                    "id", opt.getId(),
                                    "content", opt.getContent(),
                                    "isCorrect", opt.getIsCorrect()
                            ))
                            .collect(Collectors.toList())); // 将 Stream 转换为 List
                }
                return questionMap;
            }).collect(Collectors.toList()); // 将 Stream 转换为 List

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "获取题目失败：" + e.getMessage()));
        }
    }

    @GetMapping("/getClassTask/{classId}")
    public ResponseEntity<?> getClassTask(@PathVariable Long classId) {
        try {
            // 查询课程任务
            List<CourseTask> courseTasks = courseTaskRepository.findByDepartmentId(classId);
            if (courseTasks == null) {
                courseTasks = new ArrayList<>();
            }

            List<CourseTaskResponse> responseList = new ArrayList<>(); // 改用DTO列表


            // 遍历每个课程任务
            for (CourseTask courseTask : courseTasks) {
                // 查询该课程任务对应的学生任务
                List<StudentTask> studentTasks = studentTaskRepository.findByTask(courseTask);
                if (studentTasks == null) {
                    studentTasks = new ArrayList<>();
                }

                // 初始化不同状态的任务计数
                Integer notStartedCount = 0;
                Integer inProgressCount = 0;
                Integer completedCount = 0;

                // 统计不同状态的学生任务数量
                for (StudentTask studentTask : studentTasks) {
                    switch (studentTask.getStatus()) {
                        case NOT_STARTED:
                            notStartedCount++;
                            break;
                        case IN_PROGRESS:
                            inProgressCount++;
                            break;
                        case COMPLETED:
                            completedCount++;
                            break;
                    }
                }

                // 设置课程任务的不同状态计数
                // 创建DTO并填充必要数据
                CourseTaskResponse dto = new CourseTaskResponse();
                dto.setTaskId(courseTask.getId());
                dto.setTaskName(courseTask.getTitle());
                dto.setEndTime(courseTask.getEndTime());
                dto.setTaskType(courseTask.getTaskType());
                dto.setNotStartedCount(notStartedCount);
                dto.setInProgressCount(inProgressCount);
                dto.setCompletedCount(completedCount);

                responseList.add(dto);

                // 保存更新后的课程任务
                courseTaskRepository.save(courseTask);
            }

            return ResponseEntity.ok(Map.of("courseTasks", responseList));
        } catch (Exception e) {
            // 处理异常并返回错误响应
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "获取班级任务列表失败：" + e.getMessage()));
        }
    }


    private List<ExerciseQuestion> parseExercises(String exercisesJson, CourseTask task) {
        List<ExerciseQuestion> questions = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> exerciseList = objectMapper.readValue(exercisesJson, new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> exerciseData : exerciseList) {
                ExerciseQuestion question = new ExerciseQuestion();
                question.setTask(task);
                question.setType(ExerciseQuestion.QuestionType.valueOf((String) exerciseData.get("type")));
                question.setQuestion((String) exerciseData.get("question"));
                question.setAnalysis((String) exerciseData.get("analysis"));

                // 处理选项（仅选择题）
                if (question.getType() != ExerciseQuestion.QuestionType.SUBJECTIVE) {
                    List<Map<String, Object>> optionsData = (List<Map<String, Object>>) exerciseData.get("options");
                    for (Map<String, Object> optionData : optionsData) {
                        QuestionOption option = new QuestionOption();
                        option.setQuestion(question);
                        option.setContent((String) optionData.get("content"));
                        option.setIsCorrect((Boolean) optionData.get("isCorrect"));
                        question.getOptions().add(option);
                    }
                }

                questions.add(question);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析题目数据失败", e);
        }
        return questions;
    }

    // DTO类定义
    @Data // Lombok注解，自动生成getter/setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseTaskResponse {
        private Long taskId;
        private String taskName;
        private LocalDateTime endTime;
        private CourseTask.TaskType taskType;
        private Integer notStartedCount;
        private Integer inProgressCount;
        private Integer completedCount;
    }
}
