package com.cs.SmartHireAi.repository;

import com.cs.SmartHireAi.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
@RequiredArgsConstructor

public class McqTestReposistory {
    @Autowired
     JdbcTemplate  jdbcTemplate;
    public void createTest(CreateMcqTestRequest request, Long recruiterId) {

        String sql = """
        INSERT INTO mcq_tests
        (job_id, duration_minutes, total_marks, start_time, end_time, created_by)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                request.getJobId(),
                request.getDurationMinutes(),
                request.getTotalMarks(),
                request.getStartTime(),
                request.getEndTime(),
                recruiterId
        );
    }
    public void releaseTest(Long jobId) {

        String sql = """
    UPDATE mcq_tests
    SET is_released = TRUE
    WHERE job_id = ?
    """;

        jdbcTemplate.update(sql, jobId);
    }
    public McqTest findByJobId(Long jobId) {

        String sql = """
        SELECT *
        FROM mcq_tests
        WHERE job_id = ?
    """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    McqTest test = new McqTest();

                    test.setId(rs.getLong("id"));
                    test.setJobId(rs.getLong("job_id"));
                    test.setDurationMinutes(rs.getInt("duration_minutes"));
                    test.setTotalMarks(rs.getInt("total_marks"));
                    test.setStartTime(
                            rs.getTimestamp("start_time")
                                    .toLocalDateTime()
                    );
                    test.setEndTime(
                            rs.getTimestamp("end_time")
                                    .toLocalDateTime()
                    );

                    test.setReleased(
                            rs.getBoolean("is_released")
                    );

                    return test;
                },
                jobId
        );
    }

}
