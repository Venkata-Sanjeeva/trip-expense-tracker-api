package com.tripExpenseTracker.tetProject.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripExpenseTracker.tetProject.request.LoginRequest;
import com.tripExpenseTracker.tetProject.request.RegisterRequest;
import com.tripExpenseTracker.tetProject.request.ResetPasswordRequest;
import com.tripExpenseTracker.tetProject.response.GlobalResponse;
import com.tripExpenseTracker.tetProject.response.LoginResponse;
import com.tripExpenseTracker.tetProject.response.RegisterResponse;
import com.tripExpenseTracker.tetProject.service.impl.AuthUserServiceImpl;
import com.tripExpenseTracker.tetProject.service.impl.ResetPasswordService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthUserServiceImpl authUserService;
	private final ResetPasswordService resetPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
        	resetPasswordService.processForgotPassword(email);
            // We return OK even if the email doesn't exist for security
            return ResponseEntity.ok("If an account exists for " + email + ", a reset link has been sent.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
        	resetPasswordService.updatePassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password has been successfully updated.");
        } catch (IllegalArgumentException e) {
            // This catches expired or invalid tokens
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during password reset.");
        }
    }
    

    @GetMapping("/test-email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to) {
        String htmlContent = """
                <h1 style='color: #2e6c80;'>Welcome to Cat API!</h1>
                <p>This is a <strong>test email</strong> to verify our Spring Boot configuration.</p>
                <img src='https://http.cat/200' alt='cat' width='200'/>
                """;

        try {
        	resetPasswordService.sendHtmlEmail(to, "Test Email from Spring Boot", htmlContent);
            return ResponseEntity.ok("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }
	
	@PostMapping("/login")
	public ResponseEntity<GlobalResponse<LoginResponse>> loginUser(
			@RequestBody LoginRequest request) {
		
		LoginResponse loginResponse = authUserService.login(request);
		
		GlobalResponse<LoginResponse> response = GlobalResponse.<LoginResponse>builder()
		.status(HttpStatus.OK.value())
		.message("User Login Successfully...")
		.data(loginResponse)
		.build();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping("/register/{role}")
	public ResponseEntity<GlobalResponse<RegisterResponse>> registerUser(
			@RequestBody RegisterRequest request,
			@PathVariable String role) {
		
		RegisterResponse registerResponse = authUserService.register(request, role);
		
		GlobalResponse<RegisterResponse> response = GlobalResponse.<RegisterResponse>builder()
				.status(HttpStatus.OK.value())
				.message("User registered successfully...")
				.data(registerResponse)
				.build();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
