package org.example.tlonlineedupbackend.auth.controller;


import lombok.Data;
import org.example.tlonlineedupbackend.auth.entity.*;
import org.example.tlonlineedupbackend.auth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student-tasks")
public class StudentTaskController {

    @Autowired
    private StudentTaskRepository studentTaskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseTaskRepository courseTaskRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private exerciseQuestionRepository exerciseQuestionRepository;
    @Autowired
    private exerciseAnswerRepository exerciseAnswerRepository;

    @GetMapping
    public ResponseEntity<?> getStudentTasks(
            @RequestParam Long courseId,
            @RequestParam String studentId) {

        try {
            Optional<User> student = userRepository.findById(studentId);
            Optional<Course> course = courseRepository.findById(courseId);
            List<CourseTask> CourseTask = courseTaskRepository.findByCourse(course.get());
            List<StudentTask> tasks = new ArrayList<>(List.of());
            for (CourseTask courseTask : CourseTask) {
                List<StudentTask> byStudentAndTask = studentTaskRepository.findByStudentAndTask(student.get(), courseTask);
                tasks.addAll(byStudentAndTask);
            }


            List<Map<String, Object>> response = tasks.stream().map(task -> {
                CourseTask courseTask = task.getTask();
                Map<String, Object> taskMap = new HashMap<>();

                // 基础信息
                taskMap.put("id", task.getId());
                taskMap.put("CourseTaskId", task.getTask().getId());
                taskMap.put("title", courseTask.getTitle());
                taskMap.put("type", courseTask.getTaskType().name());
                taskMap.put("status", task.getStatus().name());
                taskMap.put("progress", calculateProgress(task));
                taskMap.put("endTime", courseTask.getEndTime());

                // 根据任务类型补充信息
                switch (courseTask.getTaskType()) {
                    case HOMEWORK:
                        taskMap.put("content", courseTask.getContent());
                        List<Attachment> byCourseTask = attachmentRepository.findByCourseTask(courseTask);
                        taskMap.put("attachments", byCourseTask.stream()
                                .map(this::formatAttachment)
                                .collect(Collectors.toList()));
                        break;
                    case VIDEO:
                        taskMap.put("duration", 45); // 示例值，需根据业务补充
                        break;
                    case OFFLINE:
                        if (courseTask.getOfflineSchedule() != null) {
                            taskMap.put("classroom", courseTask.getOfflineSchedule().getClassroom());
                            taskMap.put("schedule", parseSchedule(courseTask.getOfflineSchedule().getScheduleTime()));
                        }
                        break;
                    case EXERCISE:
                        taskMap.put("questionCount", courseTask.getExerciseQuestions().size());
                        break;
                }
                return taskMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "获取任务失败：" + e.getMessage()));
        }
    }

    private int calculateProgress(StudentTask task) {
        // 根据业务规则计算进度
        if (task.getStatus() == StudentTask.Status.COMPLETED) return 100;
        if (task.getStatus() == StudentTask.Status.NOT_STARTED) return 0;
        return 50; // 示例中间进度
    }

    private Map<String, Object> formatAttachment(Attachment attachment) {
        return Map.of(
                "id", attachment.getId(),
                "name", attachment.getOriginalName(),
                "size", formatFileSize(attachment.getFileSize()),
                "url", "/api/attachments/" + attachment.getId() + "/download"
        );
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        return (size / 1024) + " KB";
    }

    private List<String> parseSchedule(String scheduleTime) {
        return Arrays.asList(scheduleTime.split(";"));
    }

    @PostMapping("/updateStatus/{taskId}")
    public ResponseEntity<Void> updateStatus(@PathVariable Long taskId) {
        Optional<StudentTask> byId = studentTaskRepository.findById(taskId);
        byId.get().setStatus(StudentTask.Status.IN_PROGRESS);
        studentTaskRepository.save(byId.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasks/{taskId}/submit")
    public ResponseEntity<?> submitExercise(
            @PathVariable Long taskId,
            @RequestBody SubmissionDTO submission) {

        try {
            StudentTask studentTask = studentTaskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("任务不存在"));

            // 保存答案逻辑
            submission.getAnswers().forEach(answer -> {
                ExerciseAnswer exerciseAnswer = new ExerciseAnswer();
                exerciseAnswer.setStudentTask(studentTask);
                exerciseAnswer.setQuestion(exerciseQuestionRepository.findById(answer.getQuestionId()).get());
                exerciseAnswer.setAnswer(formatAnswer(answer));
                exerciseAnswerRepository.save(exerciseAnswer);
            });

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // StudentTaskController.java
    @PostMapping("/complete/{taskId}")
    public ResponseEntity<?> completeTask(@PathVariable Long taskId) {
        StudentTask task = studentTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        task.setStatus(StudentTask.Status.COMPLETED);
        task.setScore(100);
        studentTaskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    // 格式化答案存储
    private String formatAnswer(AnswerDTO answer) {
        switch (answer.getType()) {
            case "SINGLE":
                return answer.getValue().toString();
            case "MULTIPLE":
                if (answer.getValue() instanceof Collection) {
                    Collection<?> collection = (Collection<?>) answer.getValue();
                    return String.join(",", collection.stream()
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                } else {
                    return answer.getValue().toString();
                }
            default:
                return answer.getValue().toString();
        }
    }

    // DTO类
    @Data
    static class SubmissionDTO {
        private List<AnswerDTO> answers;
    }

    @Data
    static class AnswerDTO {
        private Long questionId;
        private String type;
        private Object value;
    }
}
