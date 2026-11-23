package mft.library.service;

import mft.library.dto.reservationdto.ReservationDto;
import mft.library.dto.reservationdto.ReservationRequest;
import mft.library.enums.LoanStatus;
import mft.library.enums.ReservationStatus;
import mft.library.exception.ApiException;
import mft.library.mapper.reservationmapper.ReservationMapper;
import mft.library.model.Book;
import mft.library.model.Member;
import mft.library.model.Reservation;
import mft.library.repository.BookRepository;
import mft.library.repository.LoanRepository;
import mft.library.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final FineService fineService;
    private final MemberService memberService;
    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository,
                              LoanRepository loanRepository, FineService fineService,
                              MemberService memberService, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.fineService = fineService;
        this.memberService = memberService;
        this.reservationMapper = reservationMapper;
    }


    @Transactional
    public ReservationDto reserve(String email, ReservationRequest request) {
        Book book = bookRepository.findByIdForUpdate(request.bookId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کتاب پیدا نشد"));
        Member member = memberService.getMemberByEmail(email);

        if (fineService.isBlocked(member.getId())) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "به‌خاطر کتاب دیرکرد یا جریمه‌ی پرداخت‌نشده نمی‌توانید رزرو کنید");
        }
        if (book.getAvailableCopies() > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "این کتاب موجود است؛ مستقیم امانت بگیرید");
        }
        if (loanRepository.existsByMemberIdAndBookIdAndStatus(member.getId(), book.getId(), LoanStatus.ACTIVE)) {
            throw new ApiException(HttpStatus.CONFLICT, "این کتاب را همین الان در امانت دارید");
        }
        if (reservationRepository.existsByMemberIdAndBookIdAndStatus(member.getId(), book.getId(), ReservationStatus.WAITING)) {
            throw new ApiException(HttpStatus.CONFLICT, "قبلاً در صف این کتاب هستید");
        }
        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setBook(book);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.WAITING);
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    /** رزروهای من. */
    @Transactional(readOnly = true)
    public List<ReservationDto> getMyReservations(String email) {
        Member member = memberService.getMemberByEmail(email);
        return reservationMapper.toDtoList(
                reservationRepository.findByMemberIdOrderByCreatedAtDesc(member.getId()));
    }
}
