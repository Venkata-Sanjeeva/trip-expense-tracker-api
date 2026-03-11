package com.tripExpenseTracker.tetProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalResponse<T> {
	private Integer status;
	private String message;
	private T data;
}
