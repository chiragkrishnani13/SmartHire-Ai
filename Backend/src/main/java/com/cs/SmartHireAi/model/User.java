package com.cs.SmartHireAi.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private int user_id;
    private String full_name;
    private String username;
    private String email;
    private String password_hash;
    private String phone_no;
    private String resume_url;
    private String ats_score;
    private int active_yn;
}

