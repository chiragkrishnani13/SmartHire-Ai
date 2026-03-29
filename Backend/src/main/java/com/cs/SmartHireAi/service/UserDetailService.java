package com.cs.SmartHireAi.service;

import com.cs.SmartHireAi.model.User;
import com.cs.SmartHireAi.reposistory.AuthReposistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private AuthReposistory authReposistory;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        System.out.println("LOGIN EMAIL RECEIVED = " + email);

        User user = authReposistory.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "User not found with email: " + email
            );
        }

        System.out.println("PASSWORD HASH FROM DB = " +
                user.getPassword_hash());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword_hash())
                .roles("USER")
                .disabled(user.getActive_yn() == 0)
                .build();
    }
}