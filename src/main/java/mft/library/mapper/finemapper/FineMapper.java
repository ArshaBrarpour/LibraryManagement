package mft.library.mapper.finemapper;

import mft.library.dto.finedto.FineDto;
import mft.library.mapper.MapperStructure.MapStructConfig;
import mft.library.mapper.basemapper.BaseMapper;
import mft.library.model.Fine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface FineMapper extends BaseMapper<Fine, FineDto> {

    @Override
    @Mapping(source = "member.id", target = "memberId")
    FineDto toDto(Fine fine);
}
