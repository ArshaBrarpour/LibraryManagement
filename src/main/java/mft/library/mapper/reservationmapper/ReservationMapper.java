package mft.library.mapper.reservationmapper;

import mft.library.dto.reservationdto.ReservationDto;
import mft.library.mapper.MapperStructure.MapStructConfig;
import mft.library.mapper.basemapper.BaseMapper;
import mft.library.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface ReservationMapper extends BaseMapper<Reservation, ReservationDto> {

    @Override
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    ReservationDto toDto(Reservation reservation);
}
