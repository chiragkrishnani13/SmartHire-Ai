package com.cs.SmartHireAi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TableRepo {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public void test(){
        jdbcTemplate.execute("""
CREATE TABLE IF NOT EXISTS mcq_answers (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
submission_id BIGINT NOT NULL,
question_id BIGINT NOT NULL,
selected_option CHAR(1),
is_correct TINYINT(1) DEFAULT 0
)
""");
    }
}
