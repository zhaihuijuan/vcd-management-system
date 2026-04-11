package top.tatobamail.backend.VcdCategories;

import java.util.List;

public interface VcdCategoriesService {

    VcdCategories createCategory(VcdCategories category);

    VcdCategories updateCategory(VcdCategories category);

    void deleteCategory(Long id);

    VcdCategories getCategoryById(Long id);

    List<VcdCategories> getAllCategories();
}
