package top.tatobamail.backend.VcdInventory.imlp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.tatobamail.backend.VcdInventory.VcdInventory;
import top.tatobamail.backend.VcdInventory.VcdInventoryRepository;
import top.tatobamail.backend.VcdInventory.VcdInventoryService;

import java.util.List;

@Service
public class VcdInventoryServiceImlp implements VcdInventoryService {

    @Autowired
    private VcdInventoryRepository vcdInventoryRepository;

    @Override
    public VcdInventory createInventory(VcdInventory inventory) {
        return vcdInventoryRepository.save(inventory);
    }

    @Override
    public VcdInventory updateInventory(VcdInventory inventory) {
        VcdInventory existing = vcdInventoryRepository.findById(inventory.getId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        existing.setStock(inventory.getStock());
        existing.setRentCount(inventory.getRentCount());
        return vcdInventoryRepository.save(existing);
    }

    @Override
    public void deleteInventory(Long id) {
        vcdInventoryRepository.deleteById(id);
    }

    @Override
    public VcdInventory getInventoryById(Long id) {
        return vcdInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    @Override
    public VcdInventory getInventoryByVcdId(Long vcdId) {
        return vcdInventoryRepository.findByVcdId(vcdId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for VCD id: " + vcdId));
    }

    @Override
    public List<VcdInventory> getAllInventories() {
        return vcdInventoryRepository.findAll();
    }
}
