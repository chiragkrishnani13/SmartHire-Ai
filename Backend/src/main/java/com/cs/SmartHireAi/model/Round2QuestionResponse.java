package com.cs.SmartHireAi.model;


import lombok.Data;
import java.util.List;

@Data
public class Round2QuestionResponse {

    private boolean eligible;

    private String message;

    private List<QuestionResponse> questions;

}