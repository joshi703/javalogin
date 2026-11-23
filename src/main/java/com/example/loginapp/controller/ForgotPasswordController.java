
package com.example.loginapp.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.loginapp.service.PasswordResetService;

@Controller
public class ForgotPasswordController {

    private static final Logger logger =
            LoggerFactory.getLogger(ForgotPasswordController.class);

    private final PasswordResetService passwordResetService;
    private final JavaMailSender mailSender;

    public ForgotPasswordController(
            PasswordResetService passwordResetService,
            JavaMailSender mailSender) {

        this.passwordResetService = passwordResetService;
        this.mailSender = mailSender;
    }

    // Show forgot password page
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    // Process forgot password request
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            HttpServletRequest request) {

        try {

            // Generate secure reset token
            String token = passwordResetService.createResetToken(email);

            // Generate reset URL
            String resetUrl = request.getScheme() + "://"
                    + request.getServerName() + ":"
                    + request.getServerPort()
                    + "/reset-password?token=" + token;

            // Prepare Gmail message
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("harshithajoshi12@gmail.com");
            message.setTo(email);
            message.setSubject(
                    "eMudhra Login Portal - Password Reset");

            message.setText(
                    "Hello,\n\n"
                    + "We received a request to reset your password.\n\n"
                    + "Click the link below to reset your password:\n\n"
                    + resetUrl + "\n\n"
                    + "This link expires in 15 minutes and can only be used once.\n\n"
                    + "If you did not request this password reset, "
                    + "please ignore this email.\n\n"
                    + "Regards,\n"
                    + "eMudhra Login Portal"
            );

            // Send email using Gmail SMTP
            mailSender.send(message);

        } catch (IllegalArgumentException e) {

            // Keep the response generic for security
            logger.warn("Password reset request could not be processed.");

        } catch (MailException e) {

            // Log email delivery failure without exposing the reset token
            logger.error("Failed to send password reset email.", e);

            return "redirect:/forgot-password?error";
        }

        return "redirect:/forgot-password?sent";
    }

    // Show reset password page
    @GetMapping("/reset-password")
    public String showResetPasswordPage(
            @RequestParam String token,
            Model model) {

        try {

            passwordResetService.validateResetToken(token);

            model.addAttribute("token", token);

            return "reset-password";

        } catch (IllegalArgumentException e) {

            return "redirect:/forgot-password?invalid";
        }
    }

    // Process new password
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        // Check password confirmation
        if (!newPassword.equals(confirmPassword)) {

            model.addAttribute("token", token);
            model.addAttribute("error", "Passwords do not match.");

            return "reset-password";
        }

        try {

            // Reset password using existing service
            passwordResetService.resetPassword(token, newPassword);

            return "redirect:/login?resetSuccess";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());

            return "reset-password";
        }
    }
}
