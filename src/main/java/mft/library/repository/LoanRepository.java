package mft.library.repository;

import jakarta.persistence.LockModeType;
import mft.library.enums.LoanStatus;
import mft.library.model.Loan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Loan l where l.id = :id")
    Optional<Loan> findByIdForUpdate(@Param("id") Long id);

    long countByMemberIdAndStatus(Long memberId, LoanStatus status);

    boolean existsByMemberIdAndBookIdAndStatus(Long memberId, Long bookId, LoanStatus status);

    boolean existsByMemberIdAndStatusAndDueDateBefore(Long memberId, LoanStatus status, LocalDateTime now);

    List<Loan> findByMemberIdAndStatusAndDueDateBefore(Long memberId, LoanStatus status, LocalDateTime now);

    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);

    long countByBookIdAndStatus(Long bookId, LoanStatus status);

    @EntityGraph(attributePaths = "book")
    List<Loan> findByMemberIdAndStatusOrderByDueDateAsc(Long memberId, LoanStatus status);

    @EntityGraph(attributePaths = "book")
    List<Loan> findByMemberIdAndStatusOrderByReturnDateDesc(Long memberId, LoanStatus status);

    void deleteByBookId(Long bookId);
}
