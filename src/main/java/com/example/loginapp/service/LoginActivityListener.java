
package com.example.loginapp.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;

@Component
public class LoginActivityListener {

    private final LoginActivityService loginActivityService;

    public LoginActivityListener(
            LoginActivityService loginActivityService) {

        this.loginActivityService = loginActivityService;
    }

    // Detect successful login
    @EventListener
    public void onLoginSuccess(AuthenticationSuccessEvent event) {

        String email = event.getAuthentication().getName();

        if (email != null && !email.isBlank()) {

            loginActivityService.recordActivity(
                    email,
                    "SUCCESS"
            );
        }
    }

    // Detect failed login
    @EventListener
    public void onLoginFailure(
            AbstractAuthenticationFailureEvent event) {

        String email = event.getAuthentication().getName();

        if (email != null && !email.isBlank()) {

            loginActivityService.recordActivity(
                    email,
                    "FAILED"
            );
        }
    }
}
