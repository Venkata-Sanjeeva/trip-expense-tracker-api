package com.tripExpenseTracker.tetProject.service.interfaces;

import java.util.Optional;

import com.tripExpenseTracker.tetProject.entity.Participant;

public interface ParticipantService {
	Optional<Participant> getParticipantByUIDAndTripUID(String participantUID, String tripUID);
}
