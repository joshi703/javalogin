
package com.example.loginapp.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.loginapp.entity.LoginActivity;
import com.example.loginapp.service.LoginActivityService;

@Controller
public class ActivityController {

    private final LoginActivityService loginActivityService;

    public ActivityController(
            LoginActivityService loginActivityService) {

        this.loginActivityService = loginActivityService;
    }

    @GetMapping("/activity")
    public String loginActivity(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        List<LoginActivity> activities =
                loginActivityService.getActivitiesByEmail(email);

        model.addAttribute("activities", activities);

        return "activity";
    }
}
