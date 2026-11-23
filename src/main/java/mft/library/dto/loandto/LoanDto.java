package mft.library.dto.loandto;

import mft.library.enums.LoanStatus;

import java.time.LocalDateTime;


public record LoanDto(
        Long id,
        Long memberId,
        Long bookId,
        String bookTitle,
        LocalDateTime loanDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        LoanStatus status
) {
}
