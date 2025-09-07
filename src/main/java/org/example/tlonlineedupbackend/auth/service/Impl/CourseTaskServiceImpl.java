package org.example.tlonlineedupbackend.auth.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.tlonlineedupbackend.auth.entity.CourseTask;
import org.example.tlonlineedupbackend.auth.exception.ResourceNotFoundException;
import org.example.tlonlineedupbackend.auth.repository.CourseTaskRepository;
import org.example.tlonlineedupbackend.auth.service.CourseTaskService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseTaskServiceImpl implements CourseTaskService {
    private final CourseTaskRepository courseTaskRepository;

    @Override
    public CourseTask getTaskById(Long taskId) {
        return courseTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));
    }

}
