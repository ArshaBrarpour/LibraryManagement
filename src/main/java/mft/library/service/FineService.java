package mft.library.service;

import mft.library.dto.finedto.DebtDto;
import mft.library.dto.finedto.FineDto;
import mft.library.enums.LoanStatus;
import mft.library.mapper.finemapper.FineMapper;
import mft.library.model.Fine;
import mft.library.model.Loan;
import mft.library.model.Member;
import mft.library.repository.FineRepository;
import mft.library.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FineService {

    // جریمه‌ی ثابت هر روز دیرکرد
    public static final long FINE_PER_DAY = 5000;

    private final FineRepository fineRepository;
    private final LoanRepository loanRepository;
    private final MemberService memberService;
    private final FineMapper fineMapper;

    public FineService(FineRepository fineRepository, LoanRepository loanRepository,
                       MemberService memberService, FineMapper fineMapper) {
        this.fineRepository = fineRepository;
        this.loanRepository = loanRepository;
        this.memberService = memberService;
        this.fineMapper = fineMapper;
    }

    public boolean isBlocked(Long memberId) {
        boolean hasOverdueBook = loanRepository.existsByMemberIdAndStatusAndDueDateBefore(
                memberId, LoanStatus.ACTIVE, LocalDateTime.now());
        boolean hasUnpaidFine = fineRepository.existsByMemberIdAndPaid(memberId, false);
        return hasOverdueBook || hasUnpaidFine;
    }

    public void createFineIfLate(Loan loan, LocalDateTime today) {
        long days = lateDays(loan.getDueDate(), today);
        if (days == 0) {
            return;
        }
        Fine fine = new Fine();
        fine.setMember(loan.getMember());
        fine.setAmount(days * FINE_PER_DAY);
        fine.setPaid(false);
        fineRepository.save(fine);
    }

    private long lateDays(LocalDateTime dueDate, LocalDateTime today) {
        long days = ChronoUnit.DAYS.between(dueDate, today);
        return Math.max(days, 0);
    }

    // ------------------------------------------------------------------
    // پنل کاربر
    // ------------------------------------------------------------------


    @Transactional(readOnly = true)
    public List<FineDto> getMyFines(String email) {
        Member member = memberService.getMemberByEmail(email);
        return fineMapper.toDtoList(fineRepository.findByMemberIdOrderByIdDesc(member.getId()));
    }


    @Transactional(readOnly = true)
    public DebtDto getMyDebt(String email) {
        Member member = memberService.getMemberByEmail(email);
        return calculateDebt(member.getId());
    }


    @Transactional
    public DebtDto payMyFines(String email) {
        Member member = memberService.getMemberByEmail(email);
        List<Fine> unpaidFines = fineRepository.findByMemberIdAndPaid(member.getId(), false);
        for (Fine fine : unpaidFines) {
            fine.setPaid(true);
        }
        return calculateDebt(member.getId());
    }


    private DebtDto calculateDebt(Long memberId) {
        LocalDateTime today = LocalDateTime.now();

        long unpaidFines = 0;
        for (Fine fine : fineRepository.findByMemberIdAndPaid(memberId, false)) {
            unpaidFines += fine.getAmount();
        }

        long overdueBooksFine = 0;
        List<Loan> overdueLoans = loanRepository.findByMemberIdAndStatusAndDueDateBefore(
                memberId, LoanStatus.ACTIVE, today);
        for (Loan loan : overdueLoans) {
            overdueBooksFine += lateDays(loan.getDueDate(), today) * FINE_PER_DAY;
        }

        return new DebtDto(unpaidFines, overdueBooksFine, unpaidFines + overdueBooksFine);
    }
}
