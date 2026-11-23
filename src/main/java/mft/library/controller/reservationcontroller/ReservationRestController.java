package mft.library.controller.reservationcontroller;

import jakarta.validation.Valid;
import mft.library.dto.reservationdto.ReservationDto;
import mft.library.dto.reservationdto.ReservationRequest;
import mft.library.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * صف رزرو کتاب‌های ناموجود (کاربر عادی)
 *
 *  POST /api/reservations      -> رزرو کتاب (body: {"bookId": 1})
 *  GET  /api/reservations/my   -> رزروهای من
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationRestController {

    private final ReservationService reservationService;

    public ReservationRestController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto reserve(Principal principal, @Valid @RequestBody ReservationRequest request) {
        return reservationService.reserve(principal.getName(), request);
    }

    @GetMapping("/my")
    public List<ReservationDto> myReservations(Principal principal) {
        return reservationService.getMyReservations(principal.getName());
    }
}
