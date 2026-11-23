package mft.library.dto.loandto;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "شناسه‌ی کتاب الزامی است")
        Long bookId
) {
}
