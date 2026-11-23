package mft.library.controller.loancontroller;

import jakarta.validation.Valid;
import mft.library.dto.loandto.LoanDto;
import mft.library.dto.loandto.LoanRequest;
import mft.library.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * امانت گرفتن و بازگرداندن کتاب (کاربر عادی)
 *
 *  POST /api/loans                  -> امانت گرفتن (body: {"bookId": 1})
 *  POST /api/loans/{loanId}/return  -> بازگرداندن کتاب
 *  GET  /api/loans/my/active        -> کتاب‌هایی که الان در امانت من است
 *  GET  /api/loans/my/history       -> تاریخچه‌ی امانت‌های قبلی من
 *
 * کاربر از روی اطلاعات ورود (Principal) مشخص می‌شود؛ هیچ‌وقت از کلاینت گرفته نمی‌شود.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanRestController {

    private final LoanService loanService;

    public LoanRestController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto borrow(Principal principal, @Valid @RequestBody LoanRequest request) {
        return loanService.loanBook(principal.getName(), request.bookId());
    }

    @PostMapping("/{loanId}/return")
    public LoanDto giveBack(Principal principal, @PathVariable("loanId") Long loanId) {
        return loanService.returnBook(principal.getName(), loanId);
    }

    @GetMapping("/my/active")
    public List<LoanDto> myActiveLoans(Principal principal) {
        return loanService.getMyActiveBorrows(principal.getName());
    }

    @GetMapping("/my/history")
    public List<LoanDto> myHistory(Principal principal) {
        return loanService.getMyHistory(principal.getName());
    }
}
