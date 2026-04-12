package com.cs.SmartHireAi.service;

import com.cs.SmartHireAi.repository.Round1Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Round1Service {

    private final Round1Repository repository;

    public void selectCandidate(Long jobId, Long applicantId) {

        repository.selectCandidate(jobId, applicantId);
    }

    public void rejectCandidate(Long jobId, Long applicantId) {

        repository.rejectCandidate(jobId, applicantId);
    }
}
