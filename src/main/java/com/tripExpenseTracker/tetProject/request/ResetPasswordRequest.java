package com.tripExpenseTracker.tetProject.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	private String token;
	private String newPassword;
}
