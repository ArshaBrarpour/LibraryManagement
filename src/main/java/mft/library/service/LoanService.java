package mft.library.service;

import mft.library.dto.loandto.LoanDto;
import mft.library.enums.LoanStatus;
import mft.library.enums.ReservationStatus;
import mft.library.exception.ApiException;
import mft.library.mapper.loanmapper.LoanMapper;
import mft.library.model.Book;
import mft.library.model.Loan;
import mft.library.model.Member;
import mft.library.model.Reservation;
import mft.library.repository.BookRepository;
import mft.library.repository.LoanRepository;
import mft.library.repository.MemberRepository;
import mft.library.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {

    private static final int LOAN_DAYS = 14;
    private static final int MAX_ACTIVE_LOANS = 3;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final FineService fineService;
    private final LoanMapper loanMapper;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository,
                       MemberRepository memberRepository, ReservationRepository reservationRepository,
                       FineService fineService, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
        this.fineService = fineService;
        this.loanMapper = loanMapper;
    }

    @Transactional
    public LoanDto loanBook(String email, Long bookId) {
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کتاب پیدا نشد"));
        Member member = memberRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کاربر پیدا نشد"));

        if (fineService.isBlocked(member.getId())) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "به‌خاطر کتاب دیرکرد یا جریمه‌ی پرداخت‌نشده نمی‌توانید امانت بگیرید");
        }
        if (loanRepository.countByMemberIdAndStatus(member.getId(), LoanStatus.ACTIVE) >= MAX_ACTIVE_LOANS) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "حداکثر " + MAX_ACTIVE_LOANS + " کتاب را می‌توانید همزمان در امانت داشته باشید");
        }
        if (loanRepository.existsByMemberIdAndBookIdAndStatus(member.getId(), bookId, LoanStatus.ACTIVE)) {
            throw new ApiException(HttpStatus.CONFLICT, "این کتاب را همین الان در امانت دارید");
        }
        if (book.getAvailableCopies() <= 0) {
            throw new ApiException(HttpStatus.CONFLICT, "موجودی این کتاب تمام شده است");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Loan loan = createLoan(member, book, LocalDateTime.now());
        return loanMapper.toDto(loan);
    }

    @Transactional
    public LoanDto returnBook(String email, Long loanId) {
        Loan loan = loanRepository.findByIdForUpdate(loanId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "امانت پیدا نشد"));
        if (!loan.getMember().getEmail().equals(email)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "امانت پیدا نشد");
        }
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new ApiException(HttpStatus.CONFLICT, "این کتاب قبلاً بازگردانده شده است");
        }
        Book book = bookRepository.findByIdForUpdate(loan.getBook().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کتاب پیدا نشد"));

        LocalDateTime today = LocalDateTime.now();
        loan.setReturnDate(today);
        loan.setStatus(LoanStatus.RETURNED);


        fineService.createFineIfLate(loan, today);


        boolean givenToQueue = giveBookToQueue(book, today);
        if (!givenToQueue) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }
        return loanMapper.toDto(loan);
    }


    @Transactional(readOnly = true)
    public List<LoanDto> getMyActiveBorrows(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کاربر پیدا نشد"));
        return loanMapper.toDtoList(
                loanRepository.findByMemberIdAndStatusOrderByDueDateAsc(member.getId(), LoanStatus.ACTIVE));
    }


    @Transactional(readOnly = true)
    public List<LoanDto> getMyHistory(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کاربر پیدا نشد"));
        return loanMapper.toDtoList(
                loanRepository.findByMemberIdAndStatusOrderByReturnDateDesc(member.getId(), LoanStatus.RETURNED));
    }

    private Loan createLoan(Member member, Book book, LocalDateTime today) {
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBook(book);
        loan.setLoanDate(today);
        loan.setDueDate(today.plusDays(LOAN_DAYS));
        loan.setStatus(LoanStatus.ACTIVE);
        return loanRepository.save(loan);
    }

    private boolean giveBookToQueue(Book book, LocalDateTime today) {
        List<Reservation> queue = reservationRepository
                .findByBookIdAndStatusOrderByCreatedAtAscIdAsc(book.getId(), ReservationStatus.WAITING);
        for (Reservation reservation : queue) {
            Member candidate = memberRepository.findByIdForUpdate(reservation.getMember().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "کاربر پیدا نشد"));
            boolean blocked = fineService.isBlocked(candidate.getId());
            boolean limitReached = loanRepository.countByMemberIdAndStatus(candidate.getId(), LoanStatus.ACTIVE)
                    >= MAX_ACTIVE_LOANS;
            boolean alreadyHasBook = loanRepository.existsByMemberIdAndBookIdAndStatus(
                    candidate.getId(), book.getId(), LoanStatus.ACTIVE);
            if (blocked || limitReached || alreadyHasBook) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                continue;
            }
            createLoan(candidate, book, today);
            reservation.setStatus(ReservationStatus.DELIVERED);
            return true;
        }
        return false;
    }
}
