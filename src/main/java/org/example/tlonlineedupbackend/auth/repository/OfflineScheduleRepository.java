package org.example.tlonlineedupbackend.auth.repository;

import org.example.tlonlineedupbackend.auth.entity.Course;
import org.example.tlonlineedupbackend.auth.entity.CourseTask;
import org.example.tlonlineedupbackend.auth.entity.OfflineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OfflineScheduleRepository extends JpaRepository<OfflineSchedule, Long> {
    Optional<OfflineSchedule> findByCourseTaskId(Long taskId);
    List<OfflineSchedule> findByCourseTask(CourseTask courseTask);
}
