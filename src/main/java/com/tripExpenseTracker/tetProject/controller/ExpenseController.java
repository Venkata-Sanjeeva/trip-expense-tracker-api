package com.tripExpenseTracker.tetProject.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	@GetMapping("/fetch/{tripUID}")
	public ResponseEntity<GlobalResponse<List<ExpenseResponse>>> getExpensesOfTrip(@PathVariable String tripUID, Principal principal) {
		return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.<List<ExpenseResponse>>builder()
				.status(HttpStatus.OK.value())
				.data(expenseService.fetchExpensesOfTripUID(tripUID))
				.message("Expenses for respective trip UID: " + tripUID + " fetched successfully...")
				.build());
	}
	
	@PutMapping("/update/{tripUID}/{expenseUID}")
	public ResponseEntity<GlobalResponse<ExpenseResponse>> updateExpenseOfTrip(
			@RequestBody ExpenseRequest expenseRequest,
			@PathVariable String tripUID,
			@PathVariable String expenseUID, 
			Principal principal) {
		return ResponseEntity.status(HttpStatus.OK).body(GlobalResponse.<ExpenseResponse>builder()
				.status(HttpStatus.OK.value())
				.data(expenseService.updateExpense(tripUID, expenseRequest, expenseUID))
				.message("Expense Saved Successfully...")
				.build());
	}
}
