package com.tripExpenseTracker.tetProject.service.interfaces;

import com.tripExpenseTracker.tetProject.request.ExpenseRequest;
import com.tripExpenseTracker.tetProject.response.ExpenseResponse;

public interface ExpenseService {
	ExpenseResponse createExpense(ExpenseRequest expenseRequest);
}
