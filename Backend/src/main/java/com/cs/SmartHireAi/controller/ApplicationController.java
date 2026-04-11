package com.cs.SmartHireAi.controller;

import com.cs.SmartHireAi.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // APPLY JOB
    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> apply(@PathVariable Long jobId, Authentication authentication) {
        try {
            applicationService.apply(jobId, authentication);
            return ResponseEntity.ok("Applied successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // MY APPLICATIONS
    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(Authentication authentication) {
        return ResponseEntity.ok(applicationService.getMyApplications(authentication));
    }

    // RECRUITER VIEW
    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getApplicants(@PathVariable Long jobId, Authentication authentication) {
        try {
            return ResponseEntity.ok(applicationService.getApplicants(jobId, authentication));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // STATUS
    @GetMapping("/status/{jobId}")
    public ResponseEntity<?> getStatus(@PathVariable Long jobId, Authentication authentication) {
        return ResponseEntity.ok(applicationService.getStatus(jobId, authentication));
    }
}