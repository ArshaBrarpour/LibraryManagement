package mft.library.dto.finedto;


public record DebtDto(
        long unpaidFines,
        long overdueBooksFine,
        long totalDebt
) {
}
