package com.tripExpenseTracker.tetProject.request;

import java.util.List;

import com.tripExpenseTracker.tetProject.response.ParticipantDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ExpenseRequest {
    @NotNull
    private String tripUID;

    @NotBlank
    private String description;

    @Positive
    private Double totalAmount;

    @NotBlank
    private String paidByParticipantUID; // The name from our participants list

    // We send the list of names who are involved in this specific bill
    private List<ParticipantDTO> involvedParticipants;
}
