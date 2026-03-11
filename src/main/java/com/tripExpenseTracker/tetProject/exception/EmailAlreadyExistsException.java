package com.tripExpenseTracker.tetProject.exception;

public class EmailAlreadyExistsException extends RuntimeException {
	public EmailAlreadyExistsException(String msg) {
		super(msg);
	}
}