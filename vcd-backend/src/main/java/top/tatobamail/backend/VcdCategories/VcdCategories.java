package top.tatobamail.backend.VcdCategories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vcd_categories")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VcdCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;
}
