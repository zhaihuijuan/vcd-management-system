package top.tatobamail.backend.VcdCategories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vcdCategories")
public class VcdCategoriesController {

    @Autowired
    private VcdCategoriesService vcdCategoriesService;

    @GetMapping
    public ResponseEntity<List<VcdCategories>> getAllCategories() {
        return ResponseEntity.ok(vcdCategoriesService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VcdCategories> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(vcdCategoriesService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<VcdCategories> createCategory(@RequestBody VcdCategories category) {
        return ResponseEntity.ok(vcdCategoriesService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VcdCategories> updateCategory(@PathVariable Long id, @RequestBody VcdCategories category) {
        category.setId(id);
        return ResponseEntity.ok(vcdCategoriesService.updateCategory(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        vcdCategoriesService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
