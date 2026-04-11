package top.tatobamail.backend.purchaseRecords;

import org.springframework.data.jpa.repository.JpaRepository;
import top.tatobamail.backend.Vcd.Vcd;

import java.util.List;

public interface PurchaseRecordsRepository extends JpaRepository<PurchaseRecords, Long> {

    List<PurchaseRecords> findByVcd(Vcd vcd);
}
