package com.tripExpenseTracker.tetProject.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExpenseOfPerson {
	private String participantName;
	
	@AllArgsConstructor
	@Data
	public static class ExpenseSplitPerPerson {
		private String expenseUID;
		private Double amountToBePaid;
		private Double totalExpenseAmount;
	}
	
	private List<ExpenseSplitPerPerson> listExpensesToBePaid;
}