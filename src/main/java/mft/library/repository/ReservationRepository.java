package mft.library.repository;

import mft.library.enums.ReservationStatus;
import mft.library.model.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByBookIdAndStatusOrderByCreatedAtAscIdAsc(Long bookId, ReservationStatus status);

    boolean existsByMemberIdAndBookIdAndStatus(Long memberId, Long bookId, ReservationStatus status);

    boolean existsByBookIdAndStatus(Long bookId, ReservationStatus status);

    @EntityGraph(attributePaths = "book")
    List<Reservation> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    void deleteByBookId(Long bookId);
}
