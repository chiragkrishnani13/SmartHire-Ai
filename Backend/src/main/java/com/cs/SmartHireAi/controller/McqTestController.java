package com.cs.SmartHireAi.controller;

import com.cs.SmartHireAi.model.CreateMcqTestRequest;
import com.cs.SmartHireAi.model.User;
import com.cs.SmartHireAi.repository.AuthRepository;
import com.cs.SmartHireAi.service.McqTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/round2/mcq")
@RequiredArgsConstructor
public class McqTestController {

    private final McqTestService service;
    @Autowired
    AuthRepository authRepository;

    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/create")
    public String createTest(
            @RequestBody CreateMcqTestRequest request,
            Authentication authentication) {
            String email  = authentication.getName();
            System.out.println(email);
           User user  = authRepository.findByEmail(email);

        Long recruiterId = user.getId(); // later extract from JWT/session

        service.createTest(request, recruiterId);

        return "MCQ Test Created Successfully";
    }
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/release/{jobId}")
    public String releaseTest(@PathVariable("jobId")  Long jobId) {

        service.releaseTest(jobId);

        return "MCQ test released successfully";
    }
    @GetMapping("/start/{jobId}")
    @PreAuthorize("hasRole('APPLICANT')")
    public String startTest(
            @PathVariable("jobId") Long jobId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user  = authRepository.findByEmail(email);
        System.out.println(user.getId());
        Long applicantId = user.getId(); // temporary (replace later with logged-in user)

        return service.startTest(jobId, applicantId);
    }
}