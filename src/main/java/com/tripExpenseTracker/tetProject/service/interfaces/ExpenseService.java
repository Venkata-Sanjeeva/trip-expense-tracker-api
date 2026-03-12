package com.tripExpenseTracker.tetProject.service.interfaces;

import java.util.List;

import com.tripExpenseTracker.tetProject.entity.Expense;
import com.tripExpenseTracker.tetProject.request.ExpenseRequest;
import com.tripExpenseTracker.tetProject.response.ExpenseResponse;

public interface ExpenseService {
	ExpenseResponse createExpense(ExpenseRequest expenseRequest);
	
	Expense fetchExpenseByUID(String expenseUID);
	
	ExpenseResponse updateExpense(String tripUID, ExpenseRequest expenseRequest, String expenseUID);
	
	List<ExpenseResponse> fetchExpensesOfTripUID(String tripUID);
}
