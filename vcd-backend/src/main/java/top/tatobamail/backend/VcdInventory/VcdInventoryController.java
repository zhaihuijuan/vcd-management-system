package top.tatobamail.backend.VcdInventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vcdInventory")
public class VcdInventoryController {

    @Autowired
    private VcdInventoryService vcdInventoryService;

    @GetMapping
    public ResponseEntity<List<VcdInventory>> getAllInventories() {
        return ResponseEntity.ok(vcdInventoryService.getAllInventories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VcdInventory> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(vcdInventoryService.getInventoryById(id));
    }

    @GetMapping("/vcd/{vcdId}")
    public ResponseEntity<VcdInventory> getInventoryByVcdId(@PathVariable Long vcdId) {
        return ResponseEntity.ok(vcdInventoryService.getInventoryByVcdId(vcdId));
    }

    @PostMapping
    public ResponseEntity<VcdInventory> createInventory(@RequestBody VcdInventory inventory) {
        return ResponseEntity.ok(vcdInventoryService.createInventory(inventory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VcdInventory> updateInventory(@PathVariable Long id, @RequestBody VcdInventory inventory) {
        inventory.setId(id);
        return ResponseEntity.ok(vcdInventoryService.updateInventory(inventory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        vcdInventoryService.deleteInventory(id);
        return ResponseEntity.ok().build();
    }
}
