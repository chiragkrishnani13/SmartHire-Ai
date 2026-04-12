package com.cs.SmartHireAi.controller;

import com.cs.SmartHireAi.model.RoundDecisionRequest;
import com.cs.SmartHireAi.service.Round1Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/round1")
@RequiredArgsConstructor
public class Round1Controller {

    private final Round1Service service;
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/select")
    public String selectCandidate(
            @RequestBody RoundDecisionRequest request) {

        service.selectCandidate(
                request.getJobId(),
                request.getApplicantId()
        );

        return "Candidate selected for Round 2";
    }
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/reject")
    public String rejectCandidate(
            @RequestBody RoundDecisionRequest request) {

        service.rejectCandidate(
                request.getJobId(),
                request.getApplicantId()
        );

        return "Candidate rejected in Round 1";
    }
}
