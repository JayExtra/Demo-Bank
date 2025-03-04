package com.dev.james.Demo.Bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "status")
    private String status;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt;
}
