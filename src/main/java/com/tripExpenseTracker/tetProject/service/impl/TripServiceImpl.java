package com.tripExpenseTracker.tetProject.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tripExpenseTracker.tetProject.entity.Participant;
import com.tripExpenseTracker.tetProject.entity.Trip;
import com.tripExpenseTracker.tetProject.entity.User;
import com.tripExpenseTracker.tetProject.repository.TripRepository;
import com.tripExpenseTracker.tetProject.request.TripRequest;
import com.tripExpenseTracker.tetProject.response.TripResponse;
import com.tripExpenseTracker.tetProject.service.interfaces.TripService;
import com.tripExpenseTracker.tetProject.util.IdentifierGenerator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
	
	private final TripRepository tripRepo;
	private final UserServiceImpl userService;
	
	@Override
	@Transactional
	public TripResponse saveTrip(TripRequest tripRequest, String userEmail) {
		
		User user = userService.getUserByEmail(userEmail);
		
		Trip trip = new Trip();
		trip.setTripUID(IdentifierGenerator.generate("trip"));
	    trip.setName(tripRequest.getTripName());
	    trip.setTripType(tripRequest.getTripType());
	    trip.setUser(user);
		
		List<Participant> participantEntities = tripRequest.getParticipants().stream()
				.map(name -> {
		            Participant p = new Participant();
		            p.setName(name);
		            p.setParticipantUID(IdentifierGenerator.generate("part"));
		            p.setTrip(trip);
		            return p;
		        }).toList();
		trip.setParticipants(participantEntities);
		    
		tripRepo.save(trip);
		    
	    TripResponse response = new TripResponse();
	    response.setTripUID(trip.getTripUID());
	    response.setTripName(trip.getName());
	    response.setTripType(trip.getTripType());
	    response.setCreatedAt(trip.getCreatedAt());
        
        // Map Participant entities back to a simple String list for React
        List<String> names = participantEntities.stream()
                .map(Participant::getName)
                .toList();
        response.setParticipants(names);
        
        return response;
	}

	@Override
	public List<TripResponse> fetchMyTrips(String userEmail) {
		// 1. Get the owner (Sanjeeva) from the Principal
	    User owner = userService.getUserByEmail(userEmail);

	    // 2. Fetch trips belonging to this user
	    List<Trip> trips = tripRepo.findByUserId(owner.getId());

	    // 3. Convert Entities to Response DTOs
	    List<TripResponse> response = trips.stream().map(trip -> {
	        TripResponse dto = TripResponse.builder()
					.tripUID(trip.getTripUID())
					.tripName(trip.getName())
					.tripType(trip.getTripType())
					.createdAt(trip.getCreatedAt())
					.build();
	        
	        // Map Participant entities back to a simple String list for React
	        List<String> names = trip.getParticipants().stream()
	                .map(Participant::getName)
	                .toList();
	        dto.setParticipants(names);
	        
	        return dto;
	    }).toList();
	    
	    return response;
	}

	@Override
	public TripResponse fetchTrip(String tripUID) {
		Trip trip = tripRepo.findByTripUID(tripUID).orElseThrow();
		
		List<String> names = trip.getParticipants().stream().map(Participant::getName).toList();
		
		return TripResponse.builder()
				.tripUID(tripUID)
				.tripName(trip.getName())
				.tripType(trip.getTripType())
				.createdAt(trip.getCreatedAt())
				.participants(names)
				.build();
	}
	
	@Override
	public void deleteTrip(String tripUID) {
		Trip trip = tripRepo.findByTripUID(tripUID).orElseThrow();
		
		tripRepo.delete(trip);
	}

	
	
}
