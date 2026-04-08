package com.cs.SmartHireAi.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class McqTest {
    private Long id;
    private Long jobId;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Long createdBy;
    private LocalDateTime createdAt;
}
