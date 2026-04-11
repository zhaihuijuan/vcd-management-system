package top.tatobamail.backend.SalesRecords;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salesRecords")
public class SalesRecordsController {

    @Autowired
    private SalesRecordsService salesRecordsService;

    @GetMapping
    public ResponseEntity<List<SaleRecords>> getAllSaleRecords() {
        return ResponseEntity.ok(salesRecordsService.getAllSaleRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleRecords> getSaleRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(salesRecordsService.getSaleRecordById(id));
    }

    @PostMapping
    public ResponseEntity<SaleRecords> createSaleRecord(@RequestBody SaleRecords saleRecord) {
        return ResponseEntity.ok(salesRecordsService.registerSaleRecord(saleRecord));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleRecords> updateSaleRecord(@PathVariable Long id, @RequestBody SaleRecords saleRecord) {
        return ResponseEntity.ok(salesRecordsService.updateSaleRecord(id, saleRecord));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleRecord(@PathVariable Long id) {
        salesRecordsService.deleteSaleRecord(id);
        return ResponseEntity.ok().build();
    }
}
