package com.tripExpenseTracker.tetProject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tripExpenseTracker.tetProject.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long>{
	Optional<Participant> findByParticipantUIDAndTripId(String participantUID, Long tripId);
	
	List<Participant> findByTripId(Long tripId);
}
