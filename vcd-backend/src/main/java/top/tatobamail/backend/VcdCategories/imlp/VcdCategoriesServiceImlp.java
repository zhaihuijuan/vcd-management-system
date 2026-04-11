package top.tatobamail.backend.VcdCategories.imlp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.tatobamail.backend.VcdCategories.VcdCategories;
import top.tatobamail.backend.VcdCategories.VcdCategoriesRepository;
import top.tatobamail.backend.VcdCategories.VcdCategoriesService;

import java.util.List;

@Service
public class VcdCategoriesServiceImlp implements VcdCategoriesService {

    @Autowired
    private VcdCategoriesRepository vcdCategoriesRepository;

    @Override
    public VcdCategories createCategory(VcdCategories category) {
        return vcdCategoriesRepository.save(category);
    }

    @Override
    public VcdCategories updateCategory(VcdCategories category) {
        VcdCategories existing = vcdCategoriesRepository.findById(category.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        return vcdCategoriesRepository.save(existing);
    }

    @Override
    public void deleteCategory(Long id) {
        vcdCategoriesRepository.deleteById(id);
    }

    @Override
    public VcdCategories getCategoryById(Long id) {
        return vcdCategoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<VcdCategories> getAllCategories() {
        return vcdCategoriesRepository.findAll();
    }
}
