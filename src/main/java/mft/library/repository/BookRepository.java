package mft.library.repository;

import jakarta.persistence.LockModeType;
import mft.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    boolean existsByIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Long id);

    @Query("select b from Book b where "
            + "(lower(b.title) like lower(concat('%', :search, '%')) "
            + " or lower(b.author) like lower(concat('%', :search, '%'))) "
            + "and (:category = '' or b.category = :category) "
            + "and (:onlyAvailable = false or b.availableCopies > 0)")
    Page<Book> search(@Param("search") String search,
                      @Param("category") String category,
                      @Param("onlyAvailable") boolean onlyAvailable,
                      Pageable pageable);
}
