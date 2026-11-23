package mft.library.mapper.basemapper;

import java.util.List;


public interface BaseMapper<E, D> {

    D toDto(E entity);

    List<D> toDtoList(List<E> entities);
}
