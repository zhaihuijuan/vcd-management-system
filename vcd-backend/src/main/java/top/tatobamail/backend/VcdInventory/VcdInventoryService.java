package top.tatobamail.backend.VcdInventory;

import java.util.List;

public interface VcdInventoryService {

    VcdInventory createInventory(VcdInventory inventory);

    VcdInventory updateInventory(VcdInventory inventory);

    void deleteInventory(Long id);

    VcdInventory getInventoryById(Long id);

    VcdInventory getInventoryByVcdId(Long vcdId);

    List<VcdInventory> getAllInventories();
}
