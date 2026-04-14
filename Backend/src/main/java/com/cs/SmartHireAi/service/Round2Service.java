package com.cs.SmartHireAi.service;

import com.cs.SmartHireAi.model.*;
import com.cs.SmartHireAi.repository.AuthRepository;
import com.cs.SmartHireAi.repository.Round2Reposistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Round2Service {
    @Autowired
    Round2Reposistory round2Reposistory;
    @Autowired
    AuthRepository authRepository;
    public Round2QuestionResponse getQuestions(
            Long testId,
            Long userId,
            Long jobId
    ) {

        Round2QuestionResponse response =
                new Round2QuestionResponse();

        String status =
                round2Reposistory.getRound1Status(
                        userId,
                        jobId
                );

        if (status == null) {

            response.setEligible(false);
            response.setMessage(
                    "You did not apply for this job"
            );

            return response;
        }

        if (!status.equalsIgnoreCase("SELECTED")) {

            response.setEligible(false);
            response.setMessage(
                    "You are not selected for Round 2"
            );

            return response;
        }

        response.setEligible(true);

        response.setQuestions(
                round2Reposistory.getQuestions(testId)
        );

        return response;
    }
    public Long startTest(
            Long userId,
            Long testId
    ) {

        return round2Reposistory.createSubmission(
                userId,
                testId
        );
    }
    public void logViolation(
            ProctorLogRequest request
    ) {

        round2Reposistory.insertLog(request);

    }
    public void submitTest(
            SubmitTestRequest request
    ) {

        int score = 0;

        for(AnswerDTO ans :
                request.getAnswers()) {

            String correctAnswer =
                    round2Reposistory.getCorrectAnswer(
                            ans.getQuestionId()
                    );

            boolean correct =
                    correctAnswer.equals(
                            ans.getSelectedOption()
                    );

            if(correct) score++;

            round2Reposistory.saveAnswer(
                    request.getSubmissionId(),
                    ans,
                    correct
            );
        }

        round2Reposistory.updateScore(
                request.getSubmissionId(),
                score
        );
    }
    public Long getJobIdFromTest(Long testId) {

        return round2Reposistory.getJobIdFromTest(testId);
    }
}
