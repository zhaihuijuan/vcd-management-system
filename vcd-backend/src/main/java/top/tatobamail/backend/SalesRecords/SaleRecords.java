package top.tatobamail.backend.SalesRecords;

import jakarta.persistence.*;
import lombok.Data;
import top.tatobamail.backend.Vcd.Vcd;
import top.tatobamail.backend.customers.customers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_records")
@Data
public class SaleRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 外键：关联 vcd 表
    @ManyToOne
    @JoinColumn(name = "vcd_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sales_records_vcd"))
    private Vcd vcd;

    // 外键：关联 customers 表
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sales_records_customer"))
    private customers customer;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private LocalDate saleDate;

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
