package top.tatobamail.backend.customers;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface customersRepository extends JpaRepository<customers, Long> {

    List<customers> findByNameContaining(String keyword);
}
