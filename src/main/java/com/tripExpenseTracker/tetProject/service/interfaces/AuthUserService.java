package com.tripExpenseTracker.tetProject.service.interfaces;

import com.tripExpenseTracker.tetProject.exception.EmailAlreadyExistsException;
import com.tripExpenseTracker.tetProject.exception.InvalidLoginCredentialsException;
import com.tripExpenseTracker.tetProject.request.LoginRequest;
import com.tripExpenseTracker.tetProject.request.RegisterRequest;
import com.tripExpenseTracker.tetProject.response.LoginResponse;
import com.tripExpenseTracker.tetProject.response.RegisterResponse;

public interface AuthUserService {
	RegisterResponse register(RegisterRequest request, String role) throws EmailAlreadyExistsException;
	
	LoginResponse login(LoginRequest request) throws InvalidLoginCredentialsException;
}