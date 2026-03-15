package com.tripExpenseTracker.tetProject.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tripExpenseTracker.tetProject.response.ExpenseOfPerson;
import com.tripExpenseTracker.tetProject.response.ParticipantDTO;
import com.tripExpenseTracker.tetProject.response.TripSplitAmountResponse;
import org.jspecify.annotations.NonNull;
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
	        .map(split -> {
				Participant participant = split.getParticipant();
				return new ExpenseResponse.SplitDetail(
						new ParticipantDTO(participant.getParticipantUID(), participant.getName()),
						split.getShareAmount());
			})
	        .collect(Collectors.toList());

		Participant paidByObj = expense.getPaidBy();
	        
	    return ExpenseResponse.builder()
	    		.expenseUID(expense.getExpenseUID())
	    		.description(expense.getDescription())
	    		.totalAmount(expense.getAmount())
	    		.paidBy(new ParticipantDTO(paidByObj.getParticipantUID(), paidByObj.getName()))
	    		.splits(listOfSplitDetail)
	    		.expenseDate(expense.getExpenseDate())
	    		.build();
	}

	private Map<String,Double> calcTotalShareAmountOfPersons(List<ExpenseResponse> expenseList) {
		Map<String,Double> map = new HashMap<>();

		for(ExpenseResponse expense : expenseList) {
			List<ExpenseResponse.SplitDetail> splitDetails = expense.getSplits();

			for(ExpenseResponse.SplitDetail split : splitDetails) {
				Double shareAmount = split.getShareAmount();
				ParticipantDTO participantDTO = split.getParticipant();

				String participantUID = participantDTO.getParticipantUID();
				map.put(participantUID, map.getOrDefault(participantUID,0.0) + shareAmount);
			}
		}
		return map;
	}

	private Map<String, Double> calcTotalAmountOfPersonsWhoPaid(List<ExpenseResponse> expenseList) {
		Map<String, Double> mapOfPaidPersons = new HashMap<>();

		for(ExpenseResponse expenseResponse : expenseList) {
			ParticipantDTO participantDTO = expenseResponse.getPaidBy();

			String paidByParticipantUID = participantDTO.getParticipantUID();
			mapOfPaidPersons.put(paidByParticipantUID, mapOfPaidPersons.getOrDefault(paidByParticipantUID, 0.0) + expenseResponse.getTotalAmount());
		}
		return mapOfPaidPersons;
	}

	private Double calcTotalAmtOfTrip(List<ExpenseResponse> expenseResponseList) {
		Double totalTripAmt = 0.0;
		for(ExpenseResponse expenseResponse : expenseResponseList) {
			totalTripAmt += expenseResponse.getTotalAmount();
		}
		return totalTripAmt;
	}

	private boolean isPartiIncludedInExpenseOrNot(List<ExpenseResponse.SplitDetail> expenseSplits, String partiUID) {
		for(ExpenseResponse.SplitDetail split : expenseSplits) {
			if(split.getParticipant().getParticipantUID().equals(partiUID)) return true;
		}
		return false;
	}

	private List<ExpenseOfPerson.ExpenseSplitPerPerson> getExpenseListOfPerson(List<ExpenseResponse> expenseList, String partUID) {
		List<ExpenseOfPerson.ExpenseSplitPerPerson> listExpensesToBePaid = new ArrayList<>();

		for(ExpenseResponse expenseResponseObj : expenseList) {
			List<ExpenseResponse.SplitDetail> splits = expenseResponseObj.getSplits();

			ExpenseOfPerson.ExpenseSplitPerPerson expenseSplitPerPerson = new ExpenseOfPerson.ExpenseSplitPerPerson(
					expenseResponseObj.getExpenseUID(),
					expenseResponseObj.getDescription(),
					0.0,
					expenseResponseObj.getTotalAmount()
			);

			if(isPartiIncludedInExpenseOrNot(splits, partUID)) {
				expenseSplitPerPerson.setAmountToBePaid(splits.get(0).getShareAmount());
			}

			listExpensesToBePaid.add(expenseSplitPerPerson);
		}

		return listExpensesToBePaid;
	}

	private List<ExpenseOfPerson> calShareAmtOfMembersInTrip(String tripUID, List<ExpenseResponse> expenseList) {
		Map<String,Double> shareAmtOfPeople = calcTotalShareAmountOfPersons(expenseList);
		Map<String, Double> paidAmtOfPeople = calcTotalAmountOfPersonsWhoPaid(expenseList);

		List<ExpenseOfPerson> expenseOfPeople = new ArrayList<>();

		for(String partiUID : shareAmtOfPeople.keySet()) {
			Participant participantObj = participantsService.getParticipantByUIDAndTripUID(partiUID, tripUID).orElseThrow(() -> new RuntimeException("Participant with UID: " + partiUID + " not found!"));

			Double participantShareAmt = shareAmtOfPeople.getOrDefault(partiUID, 0.0);
			Double participantPaidAmt = paidAmtOfPeople.getOrDefault(partiUID, 0.0);
			Double totalAmtToBePaid = Math.abs(participantPaidAmt - participantShareAmt);

			ExpenseOfPerson expenseOfPerson = new ExpenseOfPerson(
					new ParticipantDTO(partiUID, participantObj.getName()),
					participantShareAmt,
					participantPaidAmt,
					(participantPaidAmt >= participantShareAmt ? 0.0 : totalAmtToBePaid),
					getExpenseListOfPerson(expenseList, partiUID)
			);

			expenseOfPeople.add(expenseOfPerson);
		}
		return expenseOfPeople;
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

		String paidPartiUID = expenseRequest.getPaidByParticipantUID();

	    // 3. Find who paid
	    Participant payer = participantsService.getParticipantByUIDAndTripUID(paidPartiUID, trip.getTripUID()).orElseThrow(() -> new RuntimeException("Participant with UID: " + paidPartiUID + " not found!"));
	    expense.setPaidBy(payer);

	    // 4. Calculate and Create Splits (Equally for now)
	    double share = expenseRequest.getTotalAmount() / expenseRequest.getInvolvedParticipants().size();
	    
	    List<Participant> listOfParticipants = participantsService.getParticipantsByTripUID(trip.getTripUID());
	    
	    List<ExpenseSplit> splits = expenseRequest.getInvolvedParticipants().stream().map(dto -> {
	    	
	        Participant p = listOfParticipants.stream()
	            .filter(part -> part.getParticipantUID().equalsIgnoreCase(dto.getParticipantUID()))
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
	        .filter(p -> p.getParticipantUID().equalsIgnoreCase(expenseRequest.getPaidByParticipantUID()))
	        .findFirst()
	        .orElseThrow(() -> new RuntimeException("Payer not found in this trip!"));
	    expense.setPaidBy(payer);

	    // 4. Update Splits: Clear the old and add the new
	    // orphanRemoval = true in the Entity will handle the DB deletion
	    expense.getSplits().clear();

	    double share = expenseRequest.getTotalAmount() / expenseRequest.getInvolvedParticipants().size();
	    
	    for (ParticipantDTO dto : expenseRequest.getInvolvedParticipants()) {
	        Participant p = trip.getParticipants().stream()
	            .filter(part -> part.getParticipantUID().equalsIgnoreCase(dto.getParticipantUID()))
	            .findFirst()
	            .orElseThrow(() -> new RuntimeException("Participant with UID: " + dto.getParticipantUID() + " not found!"));

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
		Trip tripObj = tripService.fetchTripByUID(tripUID);
		List<ExpenseResponse> expenseList = fetchExpensesOfTripUID(tripUID);

		TripSplitAmountResponse tripSplitAmountResponse = new TripSplitAmountResponse();

		tripSplitAmountResponse.setTripUID(tripUID);
		tripSplitAmountResponse.setTripName(tripObj.getName());
		tripSplitAmountResponse.setTotalTripAmount(calcTotalAmtOfTrip(expenseList));

		List<ExpenseOfPerson> expenseOfPeople = calShareAmtOfMembersInTrip(tripUID, expenseList);

		tripSplitAmountResponse.setTripParticipants(expenseOfPeople);

		return tripSplitAmountResponse;
	}

	public Double calcShareAmountPerPerson(String participantUID, String tripUID) {
		Participant participant = participantsService.getParticipantByUIDAndTripUID(participantUID,tripUID).orElseThrow(() -> new RuntimeException("Paricipant with UID: " + participantUID + " not found!"));

		Map<String,Double> map = calcTotalShareAmountOfPersons(fetchExpensesOfTripUID(tripUID));

		return map.getOrDefault(participant.getName(),0.0);
	}

	public List<ExpenseOfPerson> fetchShareAmtOfParticipants(String tripUID) {
		List<ExpenseResponse> expenseList = fetchExpensesOfTripUID(tripUID);

		return calShareAmtOfMembersInTrip(tripUID, expenseList);
	}

}
