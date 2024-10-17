package com.example.paymob_springboot_integration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unified orderId field for all payment gateways
    @Column(name = "payment_id", nullable = true, unique = true)
    private String paymentId;

    // Replace with your user entity or use a generic user ID
    @Column(name = "user_id")
    private Long userId;

    // Replace with your item/entity being purchased
    @Column(name = "item_id")
    private Long itemId;

    @Column(nullable = false)
    private BigDecimal price;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Method method;

    public enum Status {
        PENDING,
        ACCEPTED,
        FAILED
    }

    public enum Method {
        PAYMOB_CARD,
        PAYMOB_E_WALLET
    }
}