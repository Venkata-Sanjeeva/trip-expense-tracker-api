package com.tripExpenseTracker.tetProject.exception;

public class InvalidLoginCredentialsException extends RuntimeException {
	public InvalidLoginCredentialsException(String msg) {
		super(msg);
	}
}
