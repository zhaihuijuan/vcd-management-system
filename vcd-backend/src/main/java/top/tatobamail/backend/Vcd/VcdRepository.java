package top.tatobamail.backend.Vcd;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VcdRepository extends JpaRepository<Vcd, Long> {

    Optional<Vcd> findByTitle(String title);

    boolean existsByTitle(String title);

    Page<Vcd> findByTitleContaining(String keyword, Pageable pageable);

    List<Vcd> findByTitleContaining(String keyword);

    /** 按库存租出数量排序，取热门VCD */
    @Query("SELECT v FROM Vcd v LEFT JOIN VcdInventory i ON i.vcd = v ORDER BY i.rentCount DESC")
    List<Vcd> findPopularVcds(Pageable pageable);
}
