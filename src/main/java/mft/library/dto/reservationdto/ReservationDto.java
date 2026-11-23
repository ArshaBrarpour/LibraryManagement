package mft.library.dto.reservationdto;

import mft.library.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationDto(
        Long id,
        Long memberId,
        Long bookId,
        String bookTitle,
        ReservationStatus status,
        LocalDateTime createdAt
) {
}
