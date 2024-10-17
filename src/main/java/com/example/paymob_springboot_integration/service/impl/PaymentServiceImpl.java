package com.example.paymob_springboot_integration.service.impl;

import com.example.paymob_springboot_integration.dto.PaymentInitiationDto;
import com.example.paymob_springboot_integration.entity.Payment;
import com.example.paymob_springboot_integration.exception.CustomException;
import com.example.paymob_springboot_integration.repository.PaymentRepository;
import com.example.paymob_springboot_integration.service.PaymentService;
import com.example.paymob_springboot_integration.service.ProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${PAYMOB.IFRAME_ID}")
    private String iframeId;

    @Value("${PAYMOB.API_KEY}")
    private String PAYMOB_API_KEY;

    @Value("${PAYMOB.AUTH_URL}")
    private String PAYMOB_AUTH_URL;

    @Value("${PAYMOB.ORDER_URL}")
    private String PAYMOB_ORDER_URL;

    @Value("${PAYMOB.PAYMENT_KEY_URL}")
    private String PAYMOB_PAYMENT_KEY_URL;

    @Value("${PAYMOB.CARD_INTEGRATION_ID}")
    private int cardIntegrationId;

    @Value("${PAYMOB.WALLET_INTEGRATION_ID}")
    private int walletIntegrationId;

    @Value("${PAYMOB.HMAC_SECRET}")
    private String hmacSecretKey;

    private final PaymentRepository paymentRepository;
    private final ProcessingService processingService;

    // Inject other repositories or services as needed

    @Override
    @Transactional
    public PaymentInitiationDto initiateCardPayment(Long itemId, Long userId) {
        try {
            // TODO: Implement logic to check if the user has already paid for the item
            // Example:
            // if (paymentRepository.existsByItemIdAndUserIdAndStatus(itemId, userId, PaymentStatus.ACCEPTED)) {
            //     throw new APIException(HttpStatus.BAD_REQUEST, "User has already paid for this item.");
            // }

            // TODO: Retrieve item details based on itemId
            // Example:
            // Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));

            BigDecimal price = BigDecimal.valueOf(100); // Replace with actual price retrieval

            Payment payment = new Payment();
            payment.setItemId(itemId);
            payment.setUserId(userId);
            payment.setPrice(price);
            payment.setStatus(Payment.Status.PENDING);
            payment.setMethod(Payment.Method.PAYMOB_CARD);

            // Save the payment to get an ID
            paymentRepository.save(payment);

            // Step 1: Get authentication token
            String authToken = getAuthToken();

            // Step 2: Create Paymob order
            String orderId = createPaymobOrder(authToken, payment);

            // Step 3: Generate payment key using card integration ID
            String paymentToken = generatePaymentKey(authToken, orderId, payment.getPrice(), cardIntegrationId, userId);

            // Step 4: Generate and return payment iframe URL
            String paymentLink = generatePaymentIframeUrl(paymentToken);

            PaymentInitiationDto dto = new PaymentInitiationDto();
            dto.setPaymentURL(paymentLink);
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating Paymob card payment link: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PaymentInitiationDto initiateWalletPayment(Long itemId, Long userId, String walletNumber) {
        try {
            // TODO: Implement logic to check if the user has already paid for the item
            // Example:
            // if (paymentRepository.existsByItemIdAndUserIdAndStatus(itemId, userId, PaymentStatus.ACCEPTED)) {
            //     throw new APIException(HttpStatus.BAD_REQUEST, "User has already paid for this item.");
            // }

            // TODO: Retrieve item details based on itemId
            // Example:
            // Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item", "id", itemId));

            BigDecimal price = BigDecimal.valueOf(100); // Replace with actual price retrieval

            Payment payment = new Payment();
            payment.setItemId(itemId);
            payment.setUserId(userId);
            payment.setPrice(price);
            payment.setStatus(Payment.Status.PENDING);
            payment.setMethod(Payment.Method.PAYMOB_E_WALLET);

            paymentRepository.save(payment);

            // Step 1: Get authentication token
            String authToken = getAuthToken();

            // Step 2: Create Paymob order
            String orderId = createPaymobOrder(authToken, payment);

            // Step 3: Generate payment key using wallet integration ID
            String paymentToken = generatePaymentKey(authToken, orderId, payment.getPrice(), walletIntegrationId, userId);

            // Step 4: Initiate wallet payment and return redirect URL
            String paymentLink = initiateWalletPayment(paymentToken, walletNumber);

            PaymentInitiationDto dto = new PaymentInitiationDto();
            dto.setPaymentURL(paymentLink);
            return dto;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating Paymob wallet payment link: " + e.getMessage(), e);
        }
    }

    @Override
    public void handlePaymentCallback(Map<String, Object> payload, HttpServletRequest request) {
        // TODO: Implement callback handling logic
        // This method should verify the HMAC, update payment status, and perform any post-payment actions
        // Step 1: Extract the HMAC from the request
        String receivedHmac = request.getParameter("hmac");
        System.out.println("Received HMAC: " + receivedHmac);
        if (receivedHmac == null) {
            throw new CustomException(HttpStatus.NOT_FOUND,"HMAC is missing in the request");
        }

        // Step 2: Extract the required fields from the payload
        List<String> hmacKeys = Arrays.asList(
                "amount_cents",
                "created_at",
                "currency",
                "error_occured",
                "has_parent_transaction",
                "id", // obj.id
                "integration_id",
                "is_3d_secure",
                "is_auth",
                "is_capture",
                "is_refunded",
                "is_standalone_payment",
                "is_voided",
                "order.id",
                "owner",
                "pending",
                "source_data.pan",
                "source_data.sub_type",
                "source_data.type",
                "success"
        );

        // Step 3: Concatenate the values into a single string
        String concatenatedValues = concatenateValues(payload, hmacKeys);

        // Step 4: Hash the concatenated string using your HMAC secret key
        String calculatedHmac = calculateHmac(concatenatedValues, hmacSecretKey);
        System.out.println("Calculated HMAC: " + calculatedHmac);

        // Step 5: Compare the received HMAC with the calculated HMAC
        if (!receivedHmac.equals(calculatedHmac)) {
            throw new CustomException(HttpStatus.NOT_FOUND,"Invalid HMAC signature");
        }

        // Step 6: Proceed with the logic (e.g., enroll the student)
        processingService.processPaymentCallback(payload);
    }

    // Helper methods (getAuthToken, createPaymobOrder, generatePaymentKey, generatePaymentIframeUrl, initiateWalletPayment)
    // These methods should be implemented as per Paymob's API documentation

    private String getAuthToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        requestBody.put("api_key", PAYMOB_API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(PAYMOB_AUTH_URL, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString("token");
        } else {
            throw new RuntimeException("Failed to authenticate with Paymob: " + response.getStatusCode());
        }
    }

    private String createPaymobOrder(String authToken, Payment payment) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        requestBody.put("auth_token", authToken);
        requestBody.put("delivery_needed", "false");
        requestBody.put("amount_cents", payment.getPrice().multiply(BigDecimal.valueOf(100)).intValue()); // Amount in cents
        requestBody.put("currency", "EGP"); // Replace with your currency
        requestBody.put("items", new JSONArray());

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(PAYMOB_ORDER_URL, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject jsonResponse = new JSONObject(response.getBody());

            // Store Paymob order ID in Payment entity
            String paymobOrderId = String.valueOf(jsonResponse.getInt("id"));
            payment.setPaymentId(paymobOrderId);
            paymentRepository.save(payment);

            return paymobOrderId;
        } else {
            throw new RuntimeException("Failed to create Paymob order: " + response.getStatusCode());
        }
    }

    private String generatePaymentKey(String authToken, String orderId, BigDecimal amount, int integrationId, Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject billingData = new JSONObject();
        billingData.put("email", "user@example.com"); // Replace with user's email
        billingData.put("first_name", "John"); // Replace with user's first name
        billingData.put("last_name", "Doe"); // Replace with user's last name
        billingData.put("phone_number", "1234567890"); // Replace with user's phone number

        JSONObject requestBody = new JSONObject();
        requestBody.put("auth_token", authToken);
        requestBody.put("amount_cents", amount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in cents
        requestBody.put("expiration", 3600);
        requestBody.put("order_id", orderId);
        requestBody.put("billing_data", billingData);
        requestBody.put("currency", "EGP"); // Replace with your currency
        requestBody.put("integration_id", integrationId);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(PAYMOB_PAYMENT_KEY_URL, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString("token");
        } else {
            throw new RuntimeException("Failed to generate payment key: " + response.getStatusCode());
        }
    }

    private String generatePaymentIframeUrl(String paymentToken) {
        return "https://accept.paymob.com/api/acceptance/iframes/" + iframeId + "?payment_token=" + paymentToken;
    }

    private String initiateWalletPayment(String paymentToken, String walletNumber) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject source = new JSONObject();
        source.put("identifier", walletNumber);
        source.put("subtype", "WALLET");

        JSONObject requestBody = new JSONObject();
        requestBody.put("source", source);
        requestBody.put("payment_token", paymentToken);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        String url = "https://accept.paymob.com/api/acceptance/payments/pay";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString("redirect_url");
        } else {
            throw new RuntimeException("Failed to initiate wallet payment: " + response.getStatusCode());
        }
    }

    // Additional helper methods like HMAC verification can be added here
    private String concatenateValues(Map<String, Object> payload, List<String> hmacKeys) {
        Map<String, Object> obj = (Map<String, Object>) payload.get("obj");
        if (obj == null) {
            throw new CustomException(HttpStatus.NOT_FOUND,"Invalid payload: missing 'obj' key");
        }

        StringBuilder concatenated = new StringBuilder();

        for (String key : hmacKeys) {
            String[] parts = key.split("\\.");
            Object value = null;

            if (parts.length == 1) {
                // Direct key in 'obj'
                value = obj.get(parts[0]);
            } else if (parts.length == 2) {
                // Nested key in 'obj'
                Object nestedObj = obj.get(parts[0]);
                if (nestedObj instanceof Map) {
                    value = ((Map<?, ?>) nestedObj).get(parts[1]);
                }
            }

            if (value == null) {
                value = ""; // Use empty string if value is null
            } else if (value instanceof Boolean) {
                value = value.toString(); // Convert boolean to string
            } else if (value instanceof Map || value instanceof List) {
                value = value.toString(); // Convert map or list to string
            }

            concatenated.append(value);
        }

        return concatenated.toString();
    }

    private String calculateHmac(String data, String secretKey) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte b : macData) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating HMAC", e);
        }
    }

}
