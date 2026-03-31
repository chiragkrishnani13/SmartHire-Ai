package com.cs.SmartHireAi.model;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserProfileDetails {
    private int profile_id;
    private int user_id;
    private Date date_of_birth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private  String country;
    private String pincode;
    private String linkedin_url;
    private String github_url;
    private String preferred_role;
    private String preferred_location;
    int active_yn;



}
