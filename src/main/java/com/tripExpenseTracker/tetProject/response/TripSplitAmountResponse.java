package com.tripExpenseTracker.tetProject.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripSplitAmountResponse {
	private String tripUID;
	private String tripName;
	private Double totalTripAmount;
	
	private List<ExpenseOfPerson> tripParticipants;
}
