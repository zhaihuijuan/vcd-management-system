package top.tatobamail.backend.VcdInventory;

import jakarta.persistence.*;
import lombok.Data;
import top.tatobamail.backend.Vcd.Vcd;

import java.time.LocalDateTime;

@Entity
@Table(name = "vcd_inventory")
@Data
public class VcdInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 外键：关联 vcd 表，一对一（唯一约束）
    @OneToOne
    @JoinColumn(name = "vcd_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_inventory_vcd"))
    private Vcd vcd;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int rentCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
