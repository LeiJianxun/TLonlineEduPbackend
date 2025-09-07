package org.example.tlonlineedupbackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerSubmitDTO {

    @NotNull(message = "问题ID不能为空")
    private Long questionId;

    @NotBlank(message = "答案内容不能为空")
    private String content;

    // 可选：如果需要记录提交时间
     private LocalDateTime submitTime;
}
