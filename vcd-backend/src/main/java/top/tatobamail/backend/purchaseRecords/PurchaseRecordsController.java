package top.tatobamail.backend.purchaseRecords;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchaseRecords")
public class PurchaseRecordsController {

    @Autowired
    private PurchaseRecordsService purchaseRecordsService;

    @GetMapping
    public ResponseEntity<List<PurchaseRecords>> getAllPurchaseRecords() {
        return ResponseEntity.ok(purchaseRecordsService.getAllPurchaseRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseRecords> getPurchaseRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseRecordsService.getPurchaseRecordById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseRecords> createPurchaseRecord(@RequestBody PurchaseRecords record) {
        return ResponseEntity.ok(purchaseRecordsService.createPurchaseRecord(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseRecord(@PathVariable Long id) {
        purchaseRecordsService.deletePurchaseRecord(id);
        return ResponseEntity.ok().build();
    }
}
