package com.tripExpenseTracker.tetProject.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private String description;
    private Double amount;
    private String paidBy;
    private LocalDateTime date;
    private Double sharePerPerson; // Calculated on the fly: amount / splitAmong.size()
}