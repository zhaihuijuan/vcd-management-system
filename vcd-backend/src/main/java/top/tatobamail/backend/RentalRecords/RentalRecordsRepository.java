package top.tatobamail.backend.RentalRecords;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalRecordsRepository extends JpaRepository<RentalRecords, Long> {

    List<RentalRecords> findByIsReturned(boolean isReturned);

    @Query("SELECT DISTINCT rr FROM RentalRecords rr "
            + "LEFT JOIN FETCH rr.vcd "
            + "LEFT JOIN FETCH rr.customer "
            + "WHERE rr.isReturned = true")
    List<RentalRecords> findReturnedWithAssociations();

    @Query("SELECT DISTINCT rr FROM RentalRecords rr "
            + "LEFT JOIN FETCH rr.vcd "
            + "LEFT JOIN FETCH rr.customer "
            + "WHERE rr.id = :id")
    Optional<RentalRecords> findByIdWithAssociations(@Param("id") Long id);
}
