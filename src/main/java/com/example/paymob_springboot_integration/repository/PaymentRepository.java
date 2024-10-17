package com.example.paymob_springboot_integration.repository;

import com.example.paymob_springboot_integration.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String orderId);

    // TODO: Add methods to check payment existence based on itemId and userId if needed
    // Example:
    // boolean existsByItemIdAndUserIdAndStatus(Long itemId, Long userId, Payment.Status status);
}
