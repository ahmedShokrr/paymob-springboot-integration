package com.example.paymob_springboot_integration.controller;

import com.example.paymob_springboot_integration.dto.PaymentInitiationDto;
import com.example.paymob_springboot_integration.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/card/{userId}")
    public ResponseEntity<PaymentInitiationDto> initiateCardPayment(
            @RequestParam("itemId") Long itemId,
            @PathVariable("userId") Long userId) { // find a way to get user ID from the jwt token or request
        // or according to your implementation

        PaymentInitiationDto paymentDto = paymentService.initiateCardPayment(itemId, userId);

        return ResponseEntity.ok(paymentDto);
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/wallet/{userId}")
    public ResponseEntity<PaymentInitiationDto> initiateWalletPayment(
            @RequestParam("itemId") Long itemId,
            @RequestParam("walletNumber") String walletNumber,
            @PathVariable("userId") Long userId) {// find a way to get user ID from the jwt token or request
        // or according to your implementation

        PaymentInitiationDto paymentDto = paymentService.initiateWalletPayment(itemId, userId, walletNumber);

        return ResponseEntity.ok(paymentDto);
    }
}
