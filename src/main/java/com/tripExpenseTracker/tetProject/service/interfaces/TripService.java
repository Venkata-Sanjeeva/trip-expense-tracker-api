package com.tripExpenseTracker.tetProject.service.interfaces;

import java.util.List;

import com.tripExpenseTracker.tetProject.request.TripRequest;
import com.tripExpenseTracker.tetProject.response.TripResponse;

public interface TripService {
	
	TripResponse saveTrip(TripRequest tripRequest, String userEmail);
	
	TripResponse fetchTrip(String TripUID);
	
	List<TripResponse> fetchMyTrips(String userEmail); 
	
	void deleteTrip(String tripUID);
}
