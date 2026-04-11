package top.tatobamail.backend.ReturnRecords;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReturnRecordsRepository extends JpaRepository<ReturnRecords, Long> {

    /**
     * 一次性抓取关联，避免列表序列化时懒加载未触发或代理导致前端拿不到 vcd/customer。
     */
    @Query("SELECT DISTINCT r FROM ReturnRecords r "
            + "LEFT JOIN FETCH r.vcd "
            + "LEFT JOIN FETCH r.customer "
            + "LEFT JOIN FETCH r.rental")
    List<ReturnRecords> findAllWithAssociations();

    @Query("SELECT r FROM ReturnRecords r "
            + "LEFT JOIN FETCH r.vcd "
            + "LEFT JOIN FETCH r.customer "
            + "LEFT JOIN FETCH r.rental "
            + "WHERE r.rental.id = :rentalId")
    List<ReturnRecords> findByRentalIdWithAssociations(@Param("rentalId") Long rentalId);
}
