package com.tripExpenseTracker.tetProject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tripExpenseTracker.tetProject.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
	Optional<Trip> findByTripUID(String tripUID);
	List<Trip> findByUserId(Long userId);
	Optional<Trip> findByTripUIDAndUserId(String tripUID, Long userId);
}
