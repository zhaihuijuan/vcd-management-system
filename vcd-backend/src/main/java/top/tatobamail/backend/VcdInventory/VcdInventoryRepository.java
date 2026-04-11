package top.tatobamail.backend.VcdInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import top.tatobamail.backend.Vcd.Vcd;

import java.util.Optional;

public interface VcdInventoryRepository extends JpaRepository<VcdInventory, Long> {

    Optional<VcdInventory> findByVcd(Vcd vcd);

    Optional<VcdInventory> findByVcdId(Long vcdId);
}
