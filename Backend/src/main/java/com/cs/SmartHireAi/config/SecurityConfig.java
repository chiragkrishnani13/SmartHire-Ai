package com.cs.SmartHireAi.config;

import com.cs.SmartHireAi.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    UserDetailService userDetailService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authenticationProvider(authenticationProvider())
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Public APIs
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/forget-password",
                                "/auth/valid-token",
                                "/auth/update-password",
                                "/test-db"
                        ).permitAll()


                        // Recruiter-only APIs
                        .requestMatchers("/round1/**")
                        .hasRole("RECRUITER")


                        // Applicant-only APIs
                        .requestMatchers("/round2/mcq/start")
                        .hasRole("APPLICANT")

                        .requestMatchers("/round2/mcq/test/**")
                        .hasRole("APPLICANT")

                        .requestMatchers("/round2/mcq/submit")
                        .hasRole("APPLICANT")


                        // Everything else authenticated
                        .anyRequest()
                        .authenticated()
                )


                .formLogin(form -> form

                        .loginProcessingUrl("/auth/login")

                        .usernameParameter("email")

                        .passwordParameter("password")

                        .successHandler((req, res, auth) -> {

                            res.setStatus(200);

                            res.setContentType("application/json");

                            res.getWriter().write(
                                    "{\"message\":\"Login successful\"}"
                            );
                        })


                        .failureHandler((req, res, ex) -> {

                            res.setStatus(401);

                            res.setContentType("application/json");

                            res.getWriter().write(
                                    "{\"message\":\"Invalid credentials\"}"
                            );
                        })
                );


        return http.build();
    }
}