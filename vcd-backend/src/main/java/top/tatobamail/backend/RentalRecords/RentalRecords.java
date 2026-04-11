package top.tatobamail.backend.RentalRecords;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import top.tatobamail.backend.Vcd.Vcd;
import top.tatobamail.backend.customers.customers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_records")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RentalRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 外键：关联 vcd 表
    @ManyToOne
    @JoinColumn(name = "vcd_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rental_records_vcd"))
    private Vcd vcd;

    // 外键：关联 customers 表
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rental_records_customer"))
    private customers customer;

    @Column(nullable = false)
    private float deposit;

    @Column(nullable = false)
    private LocalDate rentalDate;

    @Column
    private LocalDate expectedReturnDate;

    @Column(nullable = false)
    private boolean isReturned = false;

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
