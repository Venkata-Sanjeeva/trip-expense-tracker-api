package com.tripExpenseTracker.tetProject.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseResponse {
	private String expenseUID;
    private String description;
    private Double totalAmount;
    private ParticipantDTO paidBy;
    private LocalDateTime expenseDate;
    
    @Data
    @AllArgsConstructor
    public static class SplitDetail {
        private ParticipantDTO participant;
        private Double shareAmount;
    }
    
    // List of objects showing each person's share
    private List<SplitDetail> splits;

}
