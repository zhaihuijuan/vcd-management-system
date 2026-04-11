package top.tatobamail.backend.VcdCategories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VcdCategoriesRepository extends JpaRepository<VcdCategories, Long> {

    Optional<VcdCategories> findByName(String name);

    boolean existsByName(String name);
}
