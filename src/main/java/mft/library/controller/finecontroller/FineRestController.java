package mft.library.controller.finecontroller;

import mft.library.dto.finedto.DebtDto;
import mft.library.dto.finedto.FineDto;
import mft.library.service.FineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * دیرکرد و جریمه (کاربر عادی)
 *
 *  GET  /api/fines/my       -> لیست جریمه‌های ثبت‌شده‌ی من
 *  GET  /api/fines/my/debt  -> خلاصه‌ی بدهی من
 *  POST /api/fines/my/pay   -> تسویه‌ی شبیه‌سازی‌شده‌ی جریمه‌ها (دکمه‌ی «پرداخت»)
 */
@RestController
@RequestMapping("/api/fines")
public class FineRestController {

    private final FineService fineService;

    public FineRestController(FineService fineService) {
        this.fineService = fineService;
    }

    @GetMapping("/my")
    public List<FineDto> myFines(Principal principal) {
        return fineService.getMyFines(principal.getName());
    }

    @GetMapping("/my/debt")
    public DebtDto myDebt(Principal principal) {
        return fineService.getMyDebt(principal.getName());
    }

    @PostMapping("/my/pay")
    public DebtDto payMyFines(Principal principal) {
        return fineService.payMyFines(principal.getName());
    }
}
