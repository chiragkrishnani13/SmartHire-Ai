package com.cs.SmartHireAi.reposistory;

import com.cs.SmartHireAi.model.User;
import com.cs.SmartHireAi.model.UserProfileDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class UserProfileDetailsRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;


    public UserProfileDetails findbybirthdate(Date date_of_birth){
        String query="Select user_id,date_of_birth,gender,address,city,\n" +
                "state,country,pincode,linkedin_url,github_url,preferred_role ,\n" +
                "  preferred_location FROM user_profile WHERE date_of_birth=?";
        try {

            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> {

                UserProfileDetails userProfileDetails1=new UserProfileDetails();
                userProfileDetails1.setProfile_id(rs.getInt("profile_id"));
                userProfileDetails1.setUser_id(rs.getInt("user_id"));
                userProfileDetails1.setDate_of_birth(rs.getDate("date_of_birth"));
                userProfileDetails1.setGender(rs.getString("gender"));
                userProfileDetails1.setAddress(rs.getString("address"));
                userProfileDetails1.setCity(rs.getString("city"));
                userProfileDetails1.setState(rs.getString("state"));
                userProfileDetails1.setCountry(rs.getString("country"));
                userProfileDetails1.setPincode(rs.getString("pincode"));
                userProfileDetails1.setLinkedin_url(rs.getString("linkedin_url"));
                userProfileDetails1.setGithub_url(rs.getString("github_url"));
                userProfileDetails1.setPreferred_role(rs.getString("preferred_role"));
                userProfileDetails1.setPreferred_role(rs.getString("preferred_location"));

                return userProfileDetails1;

            }, date_of_birth);

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }
    public UserProfileDetails findbypreferred_role(String preferred_role){
        String query="Select user_id,date_of_birth,gender,address,city,\n" +
                "state,country,pincode,linkedin_url,github_url,preferred_role ,\n" +
                "  preferred_location FROM user_profile WHERE preferred_role=?";
        try {

            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> {

                UserProfileDetails userProfileDetails1=new UserProfileDetails();
                userProfileDetails1.setProfile_id(rs.getInt("profile_id"));
                userProfileDetails1.setUser_id(rs.getInt("user_id"));
                userProfileDetails1.setDate_of_birth(rs.getDate("date_of_birth"));
                userProfileDetails1.setGender(rs.getString("gender"));
                userProfileDetails1.setAddress(rs.getString("address"));
                userProfileDetails1.setCity(rs.getString("city"));
                userProfileDetails1.setState(rs.getString("state"));
                userProfileDetails1.setCountry(rs.getString("country"));
                userProfileDetails1.setPincode(rs.getString("pincode"));
                userProfileDetails1.setLinkedin_url(rs.getString("linkedin_url"));
                userProfileDetails1.setGithub_url(rs.getString("github_url"));
                userProfileDetails1.setPreferred_role(rs.getString("preferred_role"));
                userProfileDetails1.setPreferred_role(rs.getString("preferred_location"));

                return userProfileDetails1;

            }, preferred_role);

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }
    public void save(int user_id,Date date_of_birth, String gender, String address,
                     String city, String state,
                     String country, String pincode,String linkedin_url,String github_url,String preferred_role,String preferred_location){

        String query="Insert into user_profile(\n" +
                "user_id,\n" +
                "date_of_birth,\n" +
                "gender,\n" +
                "address,\n" +
                "city,\n" +
                "state,\n" +
                "country,\n" +
                "pincode,\n" +
                "linkedin_url,\n" +
                "github_url,\n" +
                "preferred_role,\n" +
                "preferred_location\n" +
                ")\n" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(query,user_id,date_of_birth,gender,address,city,state,country,pincode,linkedin_url,github_url,preferred_role,preferred_location);
    }

    public void updateProfile(UserProfileDetails userProfileDetails) {

        String sql = "UPDATE user_profile SET date_of_birth=?, gender=?, address=?, city=?, state=?, country=?, pincode=?, linkedin_url=?, github_url=?, preferred_role=?, preferred_location=? WHERE user_id=? and active_yn=1";

        jdbcTemplate.update(sql,
                userProfileDetails.getDate_of_birth(),
                userProfileDetails.getGender(),
                userProfileDetails.getAddress(),
                userProfileDetails.getCity(),
                userProfileDetails.getState(),
                userProfileDetails.getCountry(),
                userProfileDetails.getPincode(),
                userProfileDetails.getLinkedin_url(),
                userProfileDetails.getGithub_url(),
                userProfileDetails.getPreferred_role(),
                userProfileDetails.getPreferred_location(),
                userProfileDetails.getUser_id()
        );
    }
    public void saveResume(int user_id,String full_name,String email,String phone,String location,
                           String linkedin_url,String github_url,String profile_summary,String skills,String tools,String frameworks,
                           String experience,String education,String projects,String achievements,String resume_name,String resume_type,int active_yn){

        String sql = "INSERT INTO user_resume (user_id, full_name, email, phone, location, linkedin_url, github_url, " +
                "profile_summary, skills, tools, frameworks, experience, education, projects, " +
                "achievements, resume_name, resume_type,active_yn) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,user_id,full_name,email,phone,location,linkedin_url,github_url,profile_summary,skills,tools,frameworks,experience,education,projects,achievements,resume_name,resume_type,active_yn);
    }



    }

