package top.tatobamail.backend.Vcd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 与前端 vcdApi（/api/vcd）对齐的 REST 接口。
 * 旧版曾映射在 /vcd/getAll 等路径，与前端不一致会导致列表请求失败并触发安全/静态资源异常表现。
 */
@RestController
@RequestMapping("/api/vcd")
public class VcdController {

    @Autowired
    private VcdService vcdService;

    @GetMapping
    public ResponseEntity<List<Vcd>> getAllVcds() {
        return ResponseEntity.ok(vcdService.getAllVcds());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vcd>> searchVcds(@RequestParam String keyword) {
        return ResponseEntity.ok(vcdService.searchVcds(keyword));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Vcd>> getVcdPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(vcdService.getVcdPage(page, size, keyword));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Vcd>> getPopularVcds() {
        return ResponseEntity.ok(vcdService.getPopularVcds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vcd> getVcdById(@PathVariable Long id) {
        return ResponseEntity.ok(vcdService.getVcdById(id));
    }

    @PostMapping
    public ResponseEntity<Vcd> createVcd(@RequestBody Vcd vcd) {
        return ResponseEntity.ok(vcdService.createVcd(vcd));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vcd> updateVcd(@PathVariable Long id, @RequestBody Vcd vcd) {
        vcd.setId(id);
        return ResponseEntity.ok(vcdService.updateVcd(vcd));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVcd(@PathVariable Long id) {
        vcdService.deleteVcd(id);
        return ResponseEntity.ok().build();
    }
}
