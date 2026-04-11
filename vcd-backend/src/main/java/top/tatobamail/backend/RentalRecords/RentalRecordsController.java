package top.tatobamail.backend.RentalRecords;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentalRecords")
public class RentalRecordsController {

    @Autowired
    private RentalRecordsService rentalRecordsService;

    @GetMapping
    public ResponseEntity<List<RentalRecords>> getAllRentalRecords() {
        return ResponseEntity.ok(rentalRecordsService.getAllRentalRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalRecords> getRentalRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalRecordsService.getRentalRecordById(id));
    }

    @PostMapping
    public ResponseEntity<RentalRecords> createRentalRecord(@RequestBody RentalRecords record) {
        return ResponseEntity.ok(rentalRecordsService.createRentalRecord(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalRecord(@PathVariable Long id) {
        rentalRecordsService.deleteRentalRecord(id);
        return ResponseEntity.ok().build();
    }
}
