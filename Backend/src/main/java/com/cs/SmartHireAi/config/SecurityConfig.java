package com.cs.SmartHireAi.config;

import com.cs.SmartHireAi.service.UserDetailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailService userDetailService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http
                .authenticationProvider(authenticationProvider())

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/register",
                                "/login",
                                "/forget-password",
                                "/valid-token",
                                "/update-password"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginProcessingUrl("/login")

                        // 👇 THIS IS THE KEY LINE
                        .usernameParameter("email")

                        // ALWAYS keep this as password
                        .passwordParameter("password")

                        .successHandler((req, res, auth) -> {

                            res.setStatus(200);
                            res.setContentType("application/json");

                            res.getWriter().write(
                                    "{\"message\":\"Login Successful\",\"user\":\""
                                            + auth.getName()
                                            + "\"}"
                            );
                        })

                        .failureHandler((req, res, ex) -> {

                            res.setStatus(401);
                            res.setContentType("application/json");

                            res.getWriter().write(
                                    "{\"message\":\"Invalid Credentials\"}"
                            );
                        })
                );

        return http.build();
    }
}