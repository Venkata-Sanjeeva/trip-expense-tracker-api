package com.tripExpenseTracker.tetProject.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tripExpenseTracker.tetProject.response.TripSplitAmountResponse;
import org.springframework.stereotype.Service;

import com.tripExpenseTracker.tetProject.entity.Expense;
import com.tripExpenseTracker.tetProject.entity.ExpenseSplit;
import com.tripExpenseTracker.tetProject.entity.Participant;
import com.tripExpenseTracker.tetProject.entity.Trip;
import com.tripExpenseTracker.tetProject.repository.ExpenseRepository;
import com.tripExpenseTracker.tetProject.request.ExpenseRequest;
import com.tripExpenseTracker.tetProject.response.ExpenseResponse;
import com.tripExpenseTracker.tetProject.service.interfaces.ExpenseService;
import com.tripExpenseTracker.tetProject.util.IdentifierGenerator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
	private final ExpenseRepository expenseRepo;
	
	private final TripServiceImpl tripService;
	private final ParticipantServiceImpl participantsService;
	

	private ExpenseResponse mapToResponse(Expense expense) {

	    // Map the internal ExpenseSplit entities to the SplitDetail DTOs
	    List<ExpenseResponse.SplitDetail> listOfSplitDetail = expense.getSplits().stream()
	        .map(split -> new ExpenseResponse.SplitDetail(
	            split.getParticipant().getName(),
	            split.getShareAmount()
	        ))
	        .collect(Collectors.toList());
	        
	    return ExpenseResponse.builder()
	    		.expenseUID(expense.getExpenseUID())
	    		.description(expense.getDescription())
	    		.totalAmount(expense.getAmount())
	    		.paidBy(expense.getPaidBy().getName())
	    		.splits(listOfSplitDetail)
	    		.expenseDate(expense.getExpenseDate())
	    		.build();
	}

	public Map<String,Double> calcTotalAmountOfPersons(List<ExpenseResponse> expenseList) {
		Map<String,Double> map = new HashMap<>();

		for(ExpenseResponse expense : expenseList) {
			List<ExpenseResponse.SplitDetail> splitDetails = expense.getSplits();

			for(ExpenseResponse.SplitDetail split : splitDetails) {
				Double shareAmount = split.getShareAmount();
				String participantName = split.getParticipantName();
				map.put(participantName, map.getOrDefault(participantName,0.0) + shareAmount);
			}
		}
		return map;
	}

	public Map<String,Double> calcTotalAmountOfPersonsWhoPaid(List<ExpenseResponse> expenseList) {
		Map<String,Double> map = new HashMap<>();

		for(ExpenseResponse expense : expenseList) {
			String participantWhoPaid = expense.getPaidBy();

			map.put(participantWhoPaid, map.getOrDefault(participantWhoPaid,0.0) + expense.getTotalAmount());
		}
		return map;
	}
	
	@Override
	@Transactional
	public ExpenseResponse createExpense(ExpenseRequest expenseRequest) {
		// 1. Fetch the Trip
	    Trip trip = tripService.fetchTripByUID(expenseRequest.getTripUID());

	    // 2. Setup the Expense Entity
	    Expense expense = new Expense();
	    expense.setExpenseUID(IdentifierGenerator.generate("exp"));
	    expense.setDescription(expenseRequest.getDescription());
	    expense.setAmount(expenseRequest.getTotalAmount());
	    expense.setExpenseDate(LocalDateTime.now());
	    expense.setTrip(trip);

	    // 3. Find who paid
	    Participant payer = trip.getParticipants().stream()
	        .filter(p -> p.getName().toLowerCase().equals(expenseRequest.getPaidByParticipantName().toLowerCase()))
	        .findFirst().orElseThrow();
	    expense.setPaidBy(payer);

	    // 4. Calculate and Create Splits (Equally for now)
	    double share = expenseRequest.getTotalAmount() / expenseRequest.getInvolvedParticipantNames().size();
	    
	    List<Participant> listOfParticipants = participantsService.getParticipantsByTripUID(trip.getTripUID());
	    
	    List<ExpenseSplit> splits = expenseRequest.getInvolvedParticipantNames().stream().map(name -> {
	    	
	        Participant p = listOfParticipants.stream()
	            .filter(part -> part.getName().toLowerCase().equals(name.toLowerCase()))
	            .findFirst().orElseThrow(() -> new RuntimeException("Participant Not Found!"));
	            
	        ExpenseSplit split = new ExpenseSplit();
	        split.setExpense(expense);
	        split.setParticipant(p);
	        split.setShareAmount(share);
	        return split;
	    }).toList();

	    expense.setSplits(splits);
	    expenseRepo.save(expense);

	    // 5. Convert to Response DTO
	    return mapToResponse(expense);
	}
	
	@Override
	public Expense fetchExpenseByUID(String expenseUID) {
		return expenseRepo.findByExpenseUID(expenseUID).orElseThrow(() -> new RuntimeException("Expense with UID: " + expenseUID + " not found!"));
	}
	
	@Override
	@Transactional
	public ExpenseResponse updateExpense(String tripUID, ExpenseRequest expenseRequest, String expenseUID) {
	    // 1. Fetch Trip and Expense
	    Trip trip = tripService.fetchTripByUID(tripUID);
	    Expense expense = expenseRepo.findByExpenseUID(expenseUID)
	        .orElseThrow(() -> new RuntimeException("Expense not found!"));
	    
	    // 2. Update basic fields
	    expense.setDescription(expenseRequest.getDescription());
	    expense.setAmount(expenseRequest.getTotalAmount());

	    // 3. Find who paid
	    Participant payer = trip.getParticipants().stream()
	        .filter(p -> p.getName().equalsIgnoreCase(expenseRequest.getPaidByParticipantName()))
	        .findFirst()
	        .orElseThrow(() -> new RuntimeException("Payer not found in this trip!"));
	    expense.setPaidBy(payer);

	    // 4. Update Splits: Clear the old and add the new
	    // orphanRemoval = true in the Entity will handle the DB deletion
	    expense.getSplits().clear(); 

	    double share = expenseRequest.getTotalAmount() / expenseRequest.getInvolvedParticipantNames().size();
	    
	    for (String name : expenseRequest.getInvolvedParticipantNames()) {
	        Participant p = trip.getParticipants().stream()
	            .filter(part -> part.getName().equalsIgnoreCase(name))
	            .findFirst()
	            .orElseThrow(() -> new RuntimeException("Participant " + name + " not found!"));

	        ExpenseSplit split = new ExpenseSplit();
	        split.setExpense(expense);
	        split.setParticipant(p);
	        split.setShareAmount(share);
	        
	        expense.getSplits().add(split);
	    }

	    // 5. Save and Return
	    Expense savedExpense = expenseRepo.save(expense);
	    return mapToResponse(savedExpense);
	}
	
	@Override
	public List<ExpenseResponse> fetchExpensesOfTripUID(String tripUID) {
		Trip tripObj = tripService.fetchTripByUID(tripUID);
		
		List<Expense> listOfExpenses = expenseRepo.findByTripId(tripObj.getId());
		
		return listOfExpenses.stream().map(expense -> mapToResponse(expense)).toList();
		
	}

	public TripSplitAmountResponse totalExpensesOfEachParticipant(String tripUID) {
		List<ExpenseResponse> expenseList = fetchExpensesOfTripUID(tripUID);
		Map<String,Double> map = calcTotalAmountOfPersons(expenseList);

		System.out.println(map);

		return null;
	}

	public Double calcShareAmountPerPerson(String participantUID, String tripUID) {
		Participant participant = participantsService.getParticipantByUIDAndTripUID(participantUID,tripUID).orElseThrow(() -> new RuntimeException("Paricipant with UID: " + participantUID + " not found!"));

		Map<String,Double> map = calcTotalAmountOfPersons(fetchExpensesOfTripUID(tripUID));

		return map.getOrDefault(participant.getName(),0.0);
	}

}
