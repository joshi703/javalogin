
package com.example.loginapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.loginapp.entity.User;
import com.example.loginapp.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        try {
            userService.registerUser(user);
            return "redirect:/login?registered";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        model.addAttribute("email", authentication.getName());

        return "dashboard";
    }

    // Display logged-in user's profile
    @GetMapping("/profile")
    public String profile(
            Authentication authentication,
            Model model) {

        User user = userService.getUserByEmail(
                authentication.getName());

        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());

        return "profile";
    }

    // Update logged-in user's profile
    @PostMapping("/profile")
    public String updateProfile(
            Authentication authentication,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        try {

            // Identify the user using Spring Security authentication
            String currentEmail = authentication.getName();

            User updatedUser = userService.updateProfile(
                    currentEmail,
                    username,
                    email
            );

            // Check whether the email address changed
            if (!currentEmail.equals(updatedUser.getEmail())) {

                // Invalidate the old authentication session
                SecurityContextHolder.clearContext();

                if (request.getSession(false) != null) {
                    request.getSession(false).invalidate();
                }

                // User must log in with the new email
                return "redirect:/login?emailChanged";
            }

            // Username-only update: keep the user logged in
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Your profile has been updated successfully!"
            );

            return "redirect:/profile";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);

            return "profile";
        }
    }
}
