package mft.library.controller.loancontroller;

import jakarta.validation.Valid;
import mft.library.dto.loandto.LoanDto;
import mft.library.dto.loandto.LoanRequest;
import mft.library.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


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
