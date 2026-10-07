package com.tripExpenseTracker.tetProject.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedAmtResponse {

    Double totalBorrowedAmt;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AmountBorrowedParticipantObj {
        String participantUID;
        String participantName;
        Double amountBorrowed;
    }

    List<AmountBorrowedParticipantObj> listOfParticipantsAmountBorrowedFrom;
}
