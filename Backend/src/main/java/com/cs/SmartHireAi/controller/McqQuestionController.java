package com.cs.SmartHireAi.controller;

import com.cs.SmartHireAi.model.AiGeneratedQuestion;
import com.cs.SmartHireAi.model.AiMcqRequest;
import com.cs.SmartHireAi.model.CreateMcqQuestionRequest;
import com.cs.SmartHireAi.service.GeminiMcqService;
import com.cs.SmartHireAi.service.McqQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/round2/mcq/question")
@RequiredArgsConstructor
public class McqQuestionController {

    private final GeminiMcqService aiService;    private final McqQuestionService service;

    @PostMapping("/create")
    @PreAuthorize("hasRole('RECRUITER')")
    public String createQuestion(
            @RequestBody CreateMcqQuestionRequest request
    ) {

        service.createQuestion(request);

        return "MCQ Question Added Successfully";
    }
    @PostMapping("/generate-ai")
    @PreAuthorize("hasRole('RECRUITER')")
    public String generateAiQuestions(
            @RequestBody AiMcqRequest request
    ) throws Exception {

        var questions =
                aiService.generateQuestions(
                        request.getTopic(),
                        request.getDifficulty(),
                        request.getCount()
                );

        service.saveGeneratedQuestions(
                request.getTestId(),
                questions
        );

        return "AI Questions Generated and Saved Successfully";
    }
}
