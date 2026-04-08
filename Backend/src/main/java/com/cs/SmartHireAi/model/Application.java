package com.cs.SmartHireAi.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class Application {
    private Long id;
    private Long jobId;
    private Long applicantId;
    private String round1Status = "PENDING";
    private String round2Status = "PENDING";
    private String round3Status = "PENDING";
    private String round4Status = "PENDING";
    private LocalDateTime appliedAt;
}
