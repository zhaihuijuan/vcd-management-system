package top.tatobamail.backend.purchaseRecords;

import jakarta.persistence.*;
import lombok.Data;
import top.tatobamail.backend.Vcd.Vcd;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_records")
@Data
public class PurchaseRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 外键：关联 vcd 表
    @ManyToOne
    @JoinColumn(name = "vcd_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_purchase_records_vcd"))
    private Vcd vcd;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDate purchaseDate;

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
