package com.tripExpenseTracker.tetProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
	private String userUID;
	private String email;
	private String token;
	private String role;
}
