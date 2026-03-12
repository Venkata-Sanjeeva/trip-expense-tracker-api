package com.tripExpenseTracker.tetProject.service.interfaces;

import java.util.List;

import com.tripExpenseTracker.tetProject.request.ExpenseRequest;
import com.tripExpenseTracker.tetProject.response.ExpenseResponse;

public interface ExpenseService {
	ExpenseResponse createExpense(ExpenseRequest expenseRequest);
	List<ExpenseResponse> fetchExpensesOfTripUID(String tripUID);
}
