package com.tripExpenseTracker.tetProject.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class TripRequest {

    @NotBlank(message = "Trip name cannot be empty")
    private String tripName;

    @NotBlank(message = "Trip type (solo/group) is required")
    private String tripType;

    // This allows the "names only" logic from your React chips
    @NotEmpty(message = "At least one participant is required")
    private List<String> participants;
}