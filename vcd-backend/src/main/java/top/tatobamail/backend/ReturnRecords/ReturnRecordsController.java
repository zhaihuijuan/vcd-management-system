package top.tatobamail.backend.ReturnRecords;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returnRecords")
public class ReturnRecordsController {

    @Autowired
    private ReturnRecordsService returnRecordsService;

    @GetMapping
    public ResponseEntity<List<ReturnRecords>> getAllReturnRecords() {
        return ResponseEntity.ok(returnRecordsService.getAllReturnRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnRecords> getReturnRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(returnRecordsService.getReturnRecordById(id));
    }

    @PostMapping
    public ResponseEntity<ReturnRecords> createReturnRecord(@RequestBody ReturnRecords record) {
        return ResponseEntity.ok(returnRecordsService.createReturnRecord(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnRecord(@PathVariable Long id) {
        returnRecordsService.deleteReturnRecord(id);
        return ResponseEntity.ok().build();
    }
}
