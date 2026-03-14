package com.tripExpenseTracker.tetProject.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExpenseOfPerson {
	private ParticipantDTO participant;
	private Double shareAmount;
	private Double amountPaidInTrip;
	private Double totalAmountToBePaid;
	
	@AllArgsConstructor
	@Data
	public static class ExpenseSplitPerPerson {
		private String expenseUID;
		private String expenseDesc;
		private Double amountToBePaid;
		private Double totalExpenseAmount;
	}
	
	private List<ExpenseSplitPerPerson> listExpensesToBePaid;
}