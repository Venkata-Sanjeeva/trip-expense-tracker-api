package com.tripExpenseTracker.tetProject.service.impl;

import java.util.List;

import com.tripExpenseTracker.tetProject.enums.TripStatus;
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

	public TripResponse convertToResponse(String tripUID, Trip trip, List<String> participants) {
		return TripResponse.builder()
				.tripUID(tripUID)
				.tripName(trip.getName())
				.tripType(trip.getTripType())
				.tripStatus(trip.getTripStatus())
				.createdAt(trip.getCreatedAt())
				.participants(participants)
				.build();
	}

	public List<String> separateParticipantsNames(List<Participant> participants) {
		return participants.stream()
				.map(Participant::getName)
				.toList();
	}
	
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
	        // Map Participant entities back to a simple String list for React
	        List<String> names = separateParticipantsNames(trip.getParticipants());

			TripResponse responseDto = convertToResponse(trip.getTripUID(), trip, names);

			responseDto.setParticipants(names);
	        
	        return responseDto;
	    }).toList();
	    
	    return response;
	}
	
	@Override
	public Trip fetchTripByUID(String tripUID) {
		return tripRepo.findByTripUID(tripUID).orElseThrow(() -> new RuntimeException("Trip not found"));
	}

	@Override
	public TripResponse fetchTrip(String tripUID, String userEmail) {
		User user = userService.getUserByEmail(userEmail);
		
		Trip trip = tripRepo.findByTripUIDAndUserId(tripUID, user.getId()).orElseThrow();
		
		List<String> names = separateParticipantsNames(trip.getParticipants());
		
		return convertToResponse(tripUID, trip, names);
	}
	
	

	@Override
	public TripResponse updateTripStatus(String tripUID, String status, String userEmail) {
		User user = userService.getUserByEmail(userEmail);

		Trip tripObj = tripRepo.findByTripUIDAndUserId(tripUID, user.getId()).orElseThrow();

		String statusLC = status.toLowerCase();

		TripStatus tripStatus = statusLC.equals("completed") ?
					TripStatus.COMPLETED :
				statusLC.equals("active") ? TripStatus.ACTIVE: TripStatus.CREATED;

		tripObj.setTripStatus(tripStatus.toString());

		Trip savedTrip = tripRepo.save(tripObj);
		List<String> names = separateParticipantsNames(savedTrip.getParticipants());

		return convertToResponse(tripUID, savedTrip, names);
	}

	@Override
	public void deleteTrip(String tripUID) {
		Trip trip = tripRepo.findByTripUID(tripUID).orElseThrow();
		
		tripRepo.delete(trip);

		System.out.println("Trip ID: " + tripUID + " deleted successfully...");
	}


	
}
