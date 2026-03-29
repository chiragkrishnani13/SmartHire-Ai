package com.cs.SmartHireAi.service;

import com.cs.SmartHireAi.Exceptions.EmailAlreadyExistsException;
import com.cs.SmartHireAi.Exceptions.EmailInvalidException;
import com.cs.SmartHireAi.Exceptions.UsernameAlreadyExists;
import com.cs.SmartHireAi.model.User;
import com.cs.SmartHireAi.reposistory.AuthReposistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {
    @Autowired
    AuthReposistory authReposistory;
    @Autowired
    EmailService emailService;
    Logger logger = LoggerFactory.getLogger(AuthService.class);
    public void register(User user) throws EmailInvalidException, UsernameAlreadyExists, EmailAlreadyExistsException {
        if (!this.isEmailValid(user.getEmail())) {
            throw new EmailInvalidException();
        } else if (this.authReposistory.findByEmail(user.getEmail()) !=null) {
            throw new EmailAlreadyExistsException();

        } else if (this.authReposistory.findByUserName(user.getUsername()) != null) {
            logger.debug("Come here");

            throw new UsernameAlreadyExists();
        } else {

            this.authReposistory.save(user.getEmail(),user.getUsername(),user.getFull_name(),encodePassword(user.getPassword_hash()),user.getPhone_no(),user.getResume_url(),user.getAts_score());

        }
    }
    public List<Map<String,Object>> getUsers(){
        return this.authReposistory.getUsers() ;
    }
    public boolean isEmailValid(String email) {
        if(email==null) return false;
        String REGEX = "^[A-Za-z0-9_+.]+@[A-Za-z0-9_+.]+\\.[a-z]{2,}$";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private String encodePassword(String password){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }
    public User forgetPassword(Map<String,String> body) throws EmailInvalidException{
        User user = this.authReposistory.findByEmail(body.get("email"));
        System.out.println(user);
        if(user == null){
            throw new EmailInvalidException();
        }

        Map<?,?> res = this.authReposistory.checkTokenAlreadyExist(body.get("email"));
        if(res == null){
//                new
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);
            int user_id = user.getUser_id();
            authReposistory.saveResetToken(token,user_id,expiry);
        }
        else {
//                update
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);
            this.authReposistory.updateTokenExpiry(user.getUser_id(),expiry);

        }
        Map<?,?> token = this.authReposistory.updateTokenByUserID(user.getUser_id());
        emailService.sendEmail(user.getEmail(), (String) token.get("token"));

        return null;


    }
    public boolean validToken(Map<String,String> token){
        Map<?,?> res = this.authReposistory.validToken(token.get("token"));
        System.out.println(res);
        return res == null;

    }
    public boolean updatePassword(Map<String,String> data){

        Map<?,?> res = this.authReposistory.findUserByToken(data.get("token"));
        if(res == null){
            System.out.println("Token Expire HOO Chuka hai");
            return false;
        }
        else {
            System.out.println(res);
            this.authReposistory.updatePassword(this.encodePassword(data.get("password")),data.get("token"),(int) res.get("user_id"));
            return true;
        }
    }
}


