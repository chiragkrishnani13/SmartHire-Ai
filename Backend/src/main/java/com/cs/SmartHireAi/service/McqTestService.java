package com.cs.SmartHireAi.service;

import com.cs.SmartHireAi.model.CreateMcqTestRequest;
import com.cs.SmartHireAi.model.McqTest;
import com.cs.SmartHireAi.repository.McqTestReposistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class McqTestService {

    private final McqTestReposistory
            repository;

    public void createTest(CreateMcqTestRequest request, Long recruiterId) {

        if (request.getEndTime().isBefore(request.getStartTime())) {

            throw new RuntimeException("End time must be after start time");
        }

        repository.createTest(request, recruiterId);
    }
    public void releaseTest(Long jobId) {

        repository.releaseTest(jobId);
    }
    public String startTest(Long jobId, Long applicantId) {

        McqTest test = repository.findByJobId(jobId);

        if (!test.isReleased()) {
            return "Test not released yet";
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(test.getStartTime())) {
            return "Test not started yet";
        }

        if (now.isAfter(test.getEndTime())) {
            return "Time exceeded";
        }

        return "Test started successfully";
    }
}
