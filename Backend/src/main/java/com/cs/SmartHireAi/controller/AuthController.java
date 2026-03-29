package com.cs.SmartHireAi.controller;

import com.cs.SmartHireAi.Exceptions.EmailAlreadyExistsException;
import com.cs.SmartHireAi.Exceptions.EmailInvalidException;
import com.cs.SmartHireAi.Exceptions.UsernameAlreadyExists;
import com.cs.SmartHireAi.model.User;
import com.cs.SmartHireAi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        try {
            this.authService.register(user);
        }
        catch (UsernameAlreadyExists e){
            return ResponseEntity.badRequest().body(Map.of("body",e.getMessage()));
        } catch (EmailInvalidException e) {
            return ResponseEntity.badRequest().body(Map.of("body",e.getMessage()));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("body",e.getMessage()));
        }
        return ResponseEntity.ok().body(Map.of("body","Inserted Succesfully!!"));
    }
    @GetMapping("/users")
    public List<Map<String,Object>> getUsers(){
        return this.authService.getUsers();
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody Map<String,String> body)  {
        try {
            this.authService.forgetPassword(body);
        }
        catch (EmailInvalidException e){
            return ResponseEntity.badRequest().body(Map.of("body",e.getMessage()));
        }
        return ResponseEntity.ok().body(Map.of("body","User Valid!!"));
    }
    @PostMapping("valid-token")
    public ResponseEntity<?> validToken(@RequestBody Map<String,String> token) {
        if(this.authService.validToken(token)){
            System.out.println("Not Valid");
            return ResponseEntity.badRequest().body(Map.of("body","Token Time Expired"));
        }
        System.out.println("valid");
        return ResponseEntity.ok().body(Map.of("body","Token Valid!!"));
    }
    @PostMapping("update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String,String> data){
        if(this.authService.updatePassword(data)){
            return ResponseEntity.ok().body(Map.of("body","Password Updated"));
        }
        else{
            return ResponseEntity.badRequest().body(Map.of("body","Token Time Expired"));
        }


    }

}

