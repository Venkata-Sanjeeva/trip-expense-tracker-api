package com.tripExpenseTracker.tetProject.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripExpenseTracker.tetProject.request.LoginRequest;
import com.tripExpenseTracker.tetProject.request.RegisterRequest;
import com.tripExpenseTracker.tetProject.response.GlobalResponse;
import com.tripExpenseTracker.tetProject.response.LoginResponse;
import com.tripExpenseTracker.tetProject.response.RegisterResponse;
import com.tripExpenseTracker.tetProject.service.impl.AuthUserServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthUserServiceImpl authUserService;
	
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
