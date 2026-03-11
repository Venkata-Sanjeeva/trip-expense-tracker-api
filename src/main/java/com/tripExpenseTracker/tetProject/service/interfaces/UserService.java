package com.tripExpenseTracker.tetProject.service.interfaces;

import com.tripExpenseTracker.tetProject.entity.User;
import com.tripExpenseTracker.tetProject.enums.Roles;
import com.tripExpenseTracker.tetProject.exception.EmailAlreadyExistsException;
import com.tripExpenseTracker.tetProject.exception.InvalidLoginCredentialsException;
import com.tripExpenseTracker.tetProject.exception.UserNotFoundException;

public interface UserService {
	User getUserByEmail(String email) throws UserNotFoundException;
	
	User registerUser(String name, String email, String password, Roles role) throws EmailAlreadyExistsException;
	
	boolean existsByEmail(String email);
	
	boolean verifyUser(String email, String password) throws InvalidLoginCredentialsException;
}

