package mft.library.mapper.loanmapper;

import mft.library.dto.loandto.LoanDto;
import mft.library.mapper.MapperStructure.MapStructConfig;
import mft.library.mapper.basemapper.BaseMapper;
import mft.library.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface LoanMapper extends BaseMapper<Loan, LoanDto> {

    @Override
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    LoanDto toDto(Loan loan);
}
