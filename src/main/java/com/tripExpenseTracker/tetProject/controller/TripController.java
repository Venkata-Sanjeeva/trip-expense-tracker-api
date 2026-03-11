package com.tripExpenseTracker.tetProject.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tripExpenseTracker.tetProject.request.TripRequest;
import com.tripExpenseTracker.tetProject.response.GlobalResponse;
import com.tripExpenseTracker.tetProject.response.TripResponse;
import com.tripExpenseTracker.tetProject.service.impl.TripServiceImpl;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/trip")
@RestController
@RequiredArgsConstructor
public class TripController {
	
	private final TripServiceImpl tripService;
	
	@PostMapping("/create")
	public ResponseEntity<GlobalResponse<TripResponse>> createTrip(
			@RequestBody TripRequest tripRequest,
			Principal principal) {
		return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponse.<TripResponse>builder()
				.status(HttpStatus.CREATED.value())
				.data(tripService.saveTrip(tripRequest, principal.getName()))
				.message("Trip Created Successfully.")
				.build());
	}
	
	@GetMapping("/my-trips")
	public ResponseEntity<GlobalResponse<List<TripResponse>>> getUserTrips(Principal principal) {

	    return ResponseEntity.status(HttpStatus.OK)
	    		.body(GlobalResponse.<List<TripResponse>>builder()
	    				.status(200)
	    				.data(tripService.fetchMyTrips(principal.getName()))
	    				.message("User Specific Trips Fetched Successfully...")
	    				.build());
	}
}
