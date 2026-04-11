package top.tatobamail.backend.Vcd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import top.tatobamail.backend.VcdCategories.VcdCategories;

@Entity
@Table(name = "vcd")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vcd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String title;

    @Column(nullable = false, length = 255)
    private String actor;

    @Column(nullable = false, length = 255)
    private String director;

    @Column(nullable = false, length = 255)
    private String publishYear;

    @Column(nullable = false, length = 255)
    private String time;

    @Column(nullable = false)
    private float rentPrice;

    @Column(nullable = false)
    private float salesPrice;

    @Column(nullable = false, length = 255)
    private String description;

    // 外键约束：关联 vcd_categories 表
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vcd_category"))
    private VcdCategories category;
}
