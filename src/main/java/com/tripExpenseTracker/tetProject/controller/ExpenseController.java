package com.tripExpenseTracker.tetProject.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripExpenseTracker.tetProject.request.ExpenseRequest;
import com.tripExpenseTracker.tetProject.response.ExpenseResponse;
import com.tripExpenseTracker.tetProject.response.GlobalResponse;
import com.tripExpenseTracker.tetProject.service.impl.ExpenseServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
	
	private final ExpenseServiceImpl expenseService;
	
	@PostMapping("/create")
	public ResponseEntity<GlobalResponse<ExpenseResponse>> saveExpenseOfTrip(
			@RequestBody ExpenseRequest expenseRequest, Principal principal) {
		return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.<ExpenseResponse>builder()
				.status(HttpStatus.CREATED.value())
				.data(expenseService.createExpense(expenseRequest))
				.message("Expense Saved Successfully...")
				.build());
	}
	
}
