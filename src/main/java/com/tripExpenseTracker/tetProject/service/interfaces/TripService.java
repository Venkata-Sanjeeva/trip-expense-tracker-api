package com.tripExpenseTracker.tetProject.service.interfaces;

import java.util.List;

import com.tripExpenseTracker.tetProject.entity.Trip;
import com.tripExpenseTracker.tetProject.request.TripRequest;
import com.tripExpenseTracker.tetProject.response.TripResponse;

public interface TripService {
	
	Trip fetchTripByUID(String tripUID);
	
	TripResponse saveTrip(TripRequest tripRequest, String userEmail);
	
	TripResponse fetchTrip(String tripUID, String userEmail);

	TripResponse updateTripStatus(String tripUID, String status, String userEmail);

	List<TripResponse> fetchMyTrips(String userEmail); 
	
	void deleteTrip(String tripUID);
}
