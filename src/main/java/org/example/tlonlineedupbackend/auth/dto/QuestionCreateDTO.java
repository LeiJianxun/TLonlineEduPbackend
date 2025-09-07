package org.example.tlonlineedupbackend.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionCreateDTO {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "学生ID不能为空")
    private String studentId;

    @NotBlank(message = "问题内容不能为空")
    private String content;

    private List<String> keywords; // 预期答案关键词

    // 可选：添加创建时间（如果需要）
     private LocalDateTime createTime;
}
