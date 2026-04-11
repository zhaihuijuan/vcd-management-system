package top.tatobamail.backend.ReturnRecords;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import top.tatobamail.backend.RentalRecords.RentalRecords;
import top.tatobamail.backend.Vcd.Vcd;
import top.tatobamail.backend.customers.customers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "return_records")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReturnRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 外键：关联 rental_records 表
    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_return_records_rental"))
    private RentalRecords rental;

    // 外键：关联 vcd 表
    @ManyToOne
    @JoinColumn(name = "vcd_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_return_records_vcd"))
    private Vcd vcd;

    // 外键：关联 customers 表
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_return_records_customer"))
    private customers customer;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReturnStatus status;

    @Column(nullable = false)
    private int overdueDays;

    @Column(nullable = false)
    private float overdueFee;

    @Column(nullable = false)
    private float depositRefund;

    @Column(nullable = false)
    private float price;

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
