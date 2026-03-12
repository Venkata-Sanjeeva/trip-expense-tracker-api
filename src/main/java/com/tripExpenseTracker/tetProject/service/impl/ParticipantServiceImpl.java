package com.tripExpenseTracker.tetProject.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tripExpenseTracker.tetProject.entity.Participant;
import com.tripExpenseTracker.tetProject.entity.Trip;
import com.tripExpenseTracker.tetProject.repository.ParticipantRepository;
import com.tripExpenseTracker.tetProject.service.interfaces.ParticipantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
	
	private final ParticipantRepository participantRepo;
	private final TripServiceImpl tripService;
	
	@Override
	public Optional<Participant> getParticipantByUIDAndTripUID(String participantUID, String tripUID) {
		Trip tripObj = tripService.fetchTripByUID(tripUID);
		
		return participantRepo.findByParticipantUIDAndTripId(participantUID, tripObj.getId());
	}

}
