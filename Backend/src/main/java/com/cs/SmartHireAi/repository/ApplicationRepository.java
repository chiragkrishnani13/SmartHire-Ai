package com.cs.SmartHireAi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ApplicationRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // APPLY JOB
    public void apply(Long applicantId, Long jobId) {
        jdbcTemplate.update(
                "INSERT INTO applications(job_id, applicant_id, round1_status, round2_status, round3_status, round4_status, applied_at) " +
                        "VALUES (?, ?, 'PENDING', 'PENDING', 'PENDING', 'PENDING', NOW())",
                jobId, applicantId
        );
    }

    // CHECK DUPLICATE
    public Integer checkDuplicate(Long applicantId, Long jobId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM applications WHERE applicant_id=? AND job_id=?",
                Integer.class, applicantId, jobId
        );
    }

    // GET MY APPLICATIONS
    public List<Map<String, Object>> getByUser(Long applicantId) {
        return jdbcTemplate.queryForList(
                "SELECT a.*, j.title, j.location, j.job_type " +
                        "FROM applications a " +
                        "JOIN jobs j ON a.job_id = j.id " +
                        "WHERE a.applicant_id=?",
                applicantId
        );
    }

    // RECRUITER VIEW APPLICANTS
    public List<Map<String, Object>> getByJob(Long jobId) {
        return jdbcTemplate.queryForList(
                "SELECT a.*, u.name, u.email " +
                        "FROM applications a " +
                        "JOIN users u ON a.applicant_id = u.id " +
                        "WHERE a.job_id=?",
                jobId
        );
    }

    // GET STATUS
    public Map<String, Object> getStatus(Long applicantId, Long jobId) {
        return jdbcTemplate.queryForMap(
                "SELECT round1_status, round2_status, round3_status, round4_status " +
                        "FROM applications WHERE applicant_id=? AND job_id=?",
                applicantId, jobId
        );
    }
}