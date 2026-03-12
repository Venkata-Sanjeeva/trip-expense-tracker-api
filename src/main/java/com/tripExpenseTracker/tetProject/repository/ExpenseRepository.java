package com.tripExpenseTracker.tetProject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tripExpenseTracker.tetProject.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long>{
	Optional<Expense> findByExpenseUID(String expenseUID);
	List<Expense> findByTripId(Long tripId);
}
