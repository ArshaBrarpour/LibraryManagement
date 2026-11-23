package mft.library.dto.reservationdto;

import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
        @NotNull(message = "شناسه‌ی کتاب الزامی است")
        Long bookId
) {
}
