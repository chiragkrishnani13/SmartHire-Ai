package com.cs.SmartHireAi.controller;

import com.cs.SmartHireAi.model.*;
import com.cs.SmartHireAi.repository.AuthRepository;
import com.cs.SmartHireAi.service.Round2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/round2")
public class Round2Controller {
    @Autowired
    Round2Service round2Service;
    @Autowired
    AuthRepository authRepository;
    @GetMapping("/questions/{testId}")
    @PreAuthorize("hasRole('APPLICANT')")
    public Round2QuestionResponse getQuestions(
            @PathVariable(name = "testId") Long testId,
            Authentication auth
    ) {

        String email = auth.getName();

        Long userId =
                authRepository.findByEmail(email)
                        .getId();

        Long jobId =
                round2Service.getJobIdFromTest(testId);

        return round2Service.getQuestions(
                testId,
                userId,
                jobId
        );
    }
    @PostMapping("/start")
    @PreAuthorize("hasRole('APPLICANT')")
    public Long startTest(
            @RequestBody StartTestRequest request,
            Authentication auth
    ) {
        String email = auth.getName();


        Long userId =
                authRepository.findByEmail(email).getId();

        return round2Service.startTest(
                userId,
                request.getTestId()
        );
    }
    @PostMapping("/proctor/log")
    public void logViolation(
            @RequestBody ProctorLogRequest request
    ) {

        round2Service.logViolation(request);
    }
}