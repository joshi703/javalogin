
package com.example.loginapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.loginapp.entity.LoginActivity;
import com.example.loginapp.repository.LoginActivityRepository;

@Service
public class LoginActivityService {

    private final LoginActivityRepository loginActivityRepository;

    public LoginActivityService(
            LoginActivityRepository loginActivityRepository) {

        this.loginActivityRepository = loginActivityRepository;
    }

    // Save login activity
    public void recordActivity(String email, String status) {

        LoginActivity activity = new LoginActivity();

        activity.setEmail(email);
        activity.setStatus(status);
        activity.setActivityTime(LocalDateTime.now());

        loginActivityRepository.save(activity);
    }

    // Get login history of a particular user
    public List<LoginActivity> getActivitiesByEmail(String email) {

        return loginActivityRepository
                .findByEmailOrderByActivityTimeDesc(email);
    }
}
