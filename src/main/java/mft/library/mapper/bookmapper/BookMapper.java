package mft.library.mapper.bookmapper;

import mft.library.dto.bookdto.BookDto;
import mft.library.dto.bookdto.BookRequest;
import mft.library.mapper.MapperStructure.MapStructConfig;
import mft.library.mapper.basemapper.BaseMapper;
import mft.library.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(config = MapStructConfig.class)
public interface BookMapper extends BaseMapper<Book, BookDto> {

    @Override
    @Mapping(target = "available", expression = "java(book.getAvailableCopies() > 0)")
    BookDto toDto(Book book);

    Book toEntity(BookRequest request);

    void updateEntity(BookRequest request, @MappingTarget Book book);
}
