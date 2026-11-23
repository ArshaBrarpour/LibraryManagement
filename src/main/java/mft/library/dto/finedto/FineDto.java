package mft.library.dto.finedto;


public record FineDto(
        Long id,
        Long memberId,
        long amount,
        boolean paid
) {
}
