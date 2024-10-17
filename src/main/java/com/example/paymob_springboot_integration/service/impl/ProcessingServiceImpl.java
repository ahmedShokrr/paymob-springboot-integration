package com.example.paymob_springboot_integration.service.impl;


import com.example.paymob_springboot_integration.entity.Payment;
import com.example.paymob_springboot_integration.repository.PaymentRepository;
import com.example.paymob_springboot_integration.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProcessingServiceImpl implements ProcessingService {

    private final PaymentRepository paymentRepository;
    // Inject other services as needed, e.g., EnrollmentService

    @Override
    @Transactional
    public void processPaymentCallback(Map<String, Object> payload) {
        // TODO: Implement HMAC verification and payload processing
        // Example Steps:
        // 1. Verify HMAC signature
        // 2. Extract payment details from payload
        // 3. Update payment status in the database
        // 4. Perform post-payment actions (e.g., buy items, activate subscription, etc.)

        // Example placeholder logic:
        String success = (String) payload.get("success");
        String paymobOrderId = (String) payload.get("order_id");

        Payment payment = paymentRepository.findByOrderId(paymobOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if ("true".equalsIgnoreCase(success)) {
            payment.setStatus(Payment.Status.ACCEPTED);
            paymentRepository.save(payment);
            // TODO: perform other actions that should be done after successful payment
        } else {
            payment.setStatus(Payment.Status.FAILED);
            paymentRepository.save(payment);
        }
    }
}
