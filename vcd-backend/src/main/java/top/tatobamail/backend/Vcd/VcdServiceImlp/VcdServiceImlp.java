package top.tatobamail.backend.Vcd.VcdServiceImlp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import top.tatobamail.backend.Vcd.Vcd;
import top.tatobamail.backend.Vcd.VcdRepository;
import top.tatobamail.backend.Vcd.VcdService;

import java.util.List;

@Service
public class VcdServiceImlp implements VcdService {

    @Autowired
    private VcdRepository vcdRepository;

    @Override
    public Vcd createVcd(Vcd vcd) {
        return vcdRepository.save(vcd);
    }

    @Override
    public Vcd updateVcd(Vcd vcd) {
        Vcd existing = vcdRepository.findById(vcd.getId())
                .orElseThrow(() -> new RuntimeException("VCD not found"));
        existing.setTitle(vcd.getTitle());
        existing.setActor(vcd.getActor());
        existing.setDirector(vcd.getDirector());
        existing.setPublishYear(vcd.getPublishYear());
        existing.setTime(vcd.getTime());
        existing.setRentPrice(vcd.getRentPrice());
        existing.setSalesPrice(vcd.getSalesPrice());
        existing.setDescription(vcd.getDescription());
        if (vcd.getCategory() != null) {
            existing.setCategory(vcd.getCategory());
        }
        return vcdRepository.save(existing);
    }

    @Override
    public void deleteVcd(Long id) {
        vcdRepository.deleteById(id);
    }

    @Override
    public Vcd getVcdById(Long id) {
        return vcdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VCD not found"));
    }

    @Override
    public List<Vcd> getAllVcds() {
        return vcdRepository.findAll();
    }

    @Override
    public Page<Vcd> getVcdPage(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return vcdRepository.findByTitleContaining(keyword, pageable);
        }
        return vcdRepository.findAll(pageable);
    }

    @Override
    public List<Vcd> getPopularVcds() {
        Pageable top10 = PageRequest.of(0, 10);
        return vcdRepository.findPopularVcds(top10);
    }

    @Override
    public List<Vcd> searchVcds(String keyword) {
        return vcdRepository.findByTitleContaining(keyword);
    }
}
