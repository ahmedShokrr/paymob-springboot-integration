package com.example.paymob_springboot_integration.service;

import java.util.Map;

public interface ProcessingService  {
    void processPaymentCallback(Map<String, Object> payload);

}
