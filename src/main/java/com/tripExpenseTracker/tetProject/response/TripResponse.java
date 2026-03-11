package com.tripExpenseTracker.tetProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripResponse {
    private String tripUID;
    private String tripName;
    private String tripType;
    private String tripStatus;
    private LocalDateTime createdAt;
    private List<String> participants; // Just the names for the UI chips
}