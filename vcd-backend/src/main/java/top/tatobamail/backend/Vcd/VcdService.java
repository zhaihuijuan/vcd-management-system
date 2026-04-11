package top.tatobamail.backend.Vcd;

import org.springframework.data.domain.Page;

import java.util.List;

public interface VcdService {

    Vcd createVcd(Vcd vcd);

    Vcd updateVcd(Vcd vcd);

    void deleteVcd(Long id);

    Vcd getVcdById(Long id);

    List<Vcd> getAllVcds();

    Page<Vcd> getVcdPage(int page, int size, String keyword);

    List<Vcd> getPopularVcds();

    List<Vcd> searchVcds(String keyword);
}
