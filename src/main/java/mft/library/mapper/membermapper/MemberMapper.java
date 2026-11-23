package mft.library.mapper.membermapper;

import mft.library.dto.memberdto.MemberDto;
import mft.library.dto.memberdto.MemberRequest;
import mft.library.mapper.MapperStructure.MapStructConfig;
import mft.library.mapper.basemapper.BaseMapper;
import mft.library.model.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface MemberMapper extends BaseMapper<Member, MemberDto> {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    Member toEntity(MemberRequest request);
}
