package com.cs.SmartHireAi.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    JavaMailSender mailSender;
    public void sendEmail(String to,String token)  {
        String html =  String.format("""
    <h2>Password Reset</h2>
    <p>Click the link below to reset your password:</p>
    <a href="http://localhost:4200/reset-password/%s">
        Reset Password
    </a>
""", token);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        try{
            messageHelper.setTo(to);
            messageHelper.setText(token);
            messageHelper.setSubject("Reset password - Expense Tracker");
            messageHelper.setText(html,true);
            mailSender.send(message);
        }
        catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }
}

