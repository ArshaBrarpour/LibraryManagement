package mft.library.repository;

import mft.library.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findByMemberIdOrderByIdDesc(Long memberId);

    List<Fine> findByMemberIdAndPaid(Long memberId, boolean paid);

    boolean existsByMemberIdAndPaid(Long memberId, boolean paid);
}
