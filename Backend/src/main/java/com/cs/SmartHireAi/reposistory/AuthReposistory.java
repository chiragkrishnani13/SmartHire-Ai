package com.cs.SmartHireAi.reposistory;

import com.cs.SmartHireAi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class AuthReposistory {

    @Autowired
    JdbcTemplate jdbcTemplate;


    // REGISTER USER
    public void save(String email, String username, String full_name,
                     String password_hash, String phone,
                     String resume_url, String ats_score) {

        String query = """
                INSERT INTO users
                (email, username, full_name, password_hash,
                 phone, resume_url, ats_score)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(query,
                email, username, full_name,
                password_hash, phone,
                resume_url, ats_score);
    }


    // GET ACTIVE USERS
    public List<Map<String, Object>> getUsers() {

        String query = """
                SELECT user_id, email, username,
                       full_name, password_hash,
                       phone, resume_url, ats_score
                FROM users
                WHERE active_yn = 1
                """;

        return jdbcTemplate.queryForList(query);
    }


    // FIND USER BY EMAIL
    public User findByEmail(String email) {
        System.out.println("Connected DB: " +
                jdbcTemplate.queryForObject("SELECT DATABASE()", String.class));
        System.out.println("searcch email:"+email);
        String query = """
                SELECT user_id, email, username,
                       full_name, password_hash,
                       phone, resume_url,
                       ats_score, active_yn
                FROM users
                WHERE email = ?
                """;

        try {

            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> {

                User user = new User();

                user.setUser_id(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setFull_name(rs.getString("full_name"));
                user.setPassword_hash(rs.getString("password_hash"));
                user.setPhone_no(rs.getString("phone"));
                user.setResume_url(rs.getString("resume_url"));
                user.setAts_score(rs.getString("ats_score"));
                user.setActive_yn(rs.getInt("active_yn"));

                return user;

            }, email);

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }


    // FIND USER BY USERNAME
    public User findByUserName(String username) {

        String query = """
                SELECT user_id, email, username,
                       full_name, password_hash,
                       phone, resume_url,
                       ats_score, active_yn
                FROM users
                WHERE username = ?
                """;

        try {

            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> {

                User user = new User();

                user.setUser_id(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setFull_name(rs.getString("full_name"));
                user.setPassword_hash(rs.getString("password_hash"));
                user.setPhone_no(rs.getString("phone"));
                user.setResume_url(rs.getString("resume_url"));
                user.setAts_score(rs.getString("ats_score"));
                user.setActive_yn(rs.getInt("active_yn"));

                return user;

            }, username);

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }


    // SAVE RESET TOKEN
    public void saveResetToken(String token,
                               int user_id,
                               LocalDateTime expiry) {

        String query = """
                INSERT INTO auth_tokens
                (token, user_id, expiry)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(query, token, user_id, expiry);
    }


    // VALIDATE TOKEN
    public Map<String, Object> validToken(String token) {

        String query = """
                SELECT *
                FROM auth_tokens
                WHERE token = ?
                AND expiry > CURRENT_TIMESTAMP
                AND used_yn = 0
                """;

        try {

            return jdbcTemplate.queryForMap(query, token);

        } catch (Exception e) {

            return null;
        }
    }


    // UPDATE PASSWORD
    public void updatePassword(String password_hash,
                               String token,
                               int user_id) {

        String updateUser = """
                UPDATE users
                SET password_hash = ?
                WHERE user_id = ?
                """;

        String updateToken = """
                UPDATE auth_tokens
                SET used_yn = 1
                WHERE user_id = ?
                AND token = ?
                """;

        jdbcTemplate.update(updateUser,
                password_hash, user_id);

        jdbcTemplate.update(updateToken,
                user_id, token);
    }


    // FIND USER BY TOKEN
    public Map<String, Object> findUserByToken(String token) {

        String query = """
                SELECT user_id
                FROM auth_tokens
                WHERE token = ?
                AND expiry > CURRENT_TIMESTAMP
                AND used_yn = 0
                """;

        try {

            return jdbcTemplate.queryForMap(query, token);

        } catch (Exception e) {

            return null;
        }
    }


    // CHECK IF TOKEN ALREADY EXISTS
    public Map<String, Object> checkTokenAlreadyExist(String email) {

        String query = """
                SELECT users.user_id
                FROM users
                INNER JOIN auth_tokens
                ON users.user_id = auth_tokens.user_id
                WHERE users.email = ?
                AND auth_tokens.used_yn = 0
                """;

        try {

            return jdbcTemplate.queryForMap(query, email);

        } catch (Exception e) {

            return null;
        }
    }


    // UPDATE TOKEN EXPIRY
    public int updateTokenExpiry(int user_id,
                                 LocalDateTime expiry) {

        String query = """
                UPDATE auth_tokens
                SET expiry = ?
                WHERE user_id = ?
                """;

        return jdbcTemplate.update(query,
                expiry, user_id);
    }


    // GET TOKEN BY USER ID
    public Map<String, Object> updateTokenByUserID(int user_id) {

        String query = """
                SELECT token
                FROM auth_tokens
                WHERE user_id = ?
                """;

        return jdbcTemplate.queryForMap(query,
                user_id);
    }

}