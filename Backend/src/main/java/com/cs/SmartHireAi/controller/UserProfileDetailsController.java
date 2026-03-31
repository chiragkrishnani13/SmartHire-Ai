package com.cs.SmartHireAi.controller;


import com.cs.SmartHireAi.Exceptions.DateOfBirthDoesNotExist;
import com.cs.SmartHireAi.Exceptions.RoleNotSpecefied;
import com.cs.SmartHireAi.Exceptions.UserDoesNotExist;
import com.cs.SmartHireAi.model.UserProfileDetails;
import com.cs.SmartHireAi.model.UserResume;
import com.cs.SmartHireAi.service.UserProfileDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;

@RestController
public class UserProfileDetailsController {
    @Autowired
    UserProfileDetailsService userProfileDetailsService;
    private static Logger logger= LoggerFactory.getLogger(UserProfileDetailsController.class);

        @PostMapping("/userprofiledetails")
    public ResponseEntity<?> userprofiledetails(@RequestBody UserProfileDetails userProfileDetails, Authentication authentication){
//          authentication leke ayega peche se email
        System.out.println(authentication.getName());
        try{
            userProfileDetailsService.SaveUserDetails(userProfileDetails,authentication);
//            categoryService.savecategory(category,authentication.getName());
            return ResponseEntity.ok().body(Map.of("body","UserProfileDetails entired Successfully"));
        }
        catch(UserDoesNotExist e){
            logger.info(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("body",e.getMessage()));
        }
        catch(DateOfBirthDoesNotExist e){
            logger.info(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("body",e.getMessage()));
        }
        catch(RoleNotSpecefied e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("body",e.getMessage()));
        }
        catch(Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("body","Some error occured"));

        }
    }
    @PutMapping("/update/{user_id}")
    public ResponseEntity<?>updateuserprofiledetails(@PathVariable(name="user_id")int user_id, @RequestBody UserProfileDetails userProfileDetails,Authentication authentication){
            try{
                userProfileDetailsService.updateuserprofile(userProfileDetails,authentication);
                return ResponseEntity.ok().body(Map.of("body","UserProfileDetails updated Successfully"));


            }
            catch(Exception e){
                logger.error(e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("body","Some error occured"));

            }
    }
    @PostMapping("/upload-resume")
    public ResponseEntity<?> savresume(@RequestParam("file") MultipartFile file, UserResume userResume,
            Authentication authentication) {
        try {
            userProfileDetailsService.saveresume(file, userResume,authentication);
            return ResponseEntity.ok("Resume uploaded successfully ✅");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }






}
