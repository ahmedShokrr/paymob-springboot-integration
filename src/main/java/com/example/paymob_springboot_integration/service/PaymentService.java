package com.example.paymob_springboot_integration.service;


import com.example.paymob_springboot_integration.dto.PaymentInitiationDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    PaymentInitiationDto initiateCardPayment(Long itemId, Long userId);

    PaymentInitiationDto initiateWalletPayment(Long itemId, Long userId, String walletNumber);

    void handlePaymentCallback(Map<String, Object> payload, HttpServletRequest request);


}
