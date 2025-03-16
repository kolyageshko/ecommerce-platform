package com.citadelcult.citadelcult.payment;

import com.citadelcult.citadelcult.order.entities.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class FondyPaymentProcessor implements PaymentProcessor {

    @Value("${fondy.merchantId}")
    private String merchantId;

    @Value("${fondy.merchantPassword}")
    private String merchantPassword;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${backend.url}")
    private String backendUrl;

    private static final String FONDY_API_CHECKOUT_URL = "https://pay.fondy.eu/api/checkout/url/";
    private static final String SIGNATURE_KEY = "signature";
    private static final String RESPONSE_SIGNATURE_STRING_KEY = "response_signature_string";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String getPaymentUrl(Order order) {
        var params = getPayParameters(order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("request", params);

        String jsonParams = objectMapper.writeValueAsString(requestParams);
        HttpEntity<String> entity = new HttpEntity<>(jsonParams, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(FONDY_API_CHECKOUT_URL, entity, String.class);
        String responseBody = responseEntity.getBody();
        if (responseBody != null) {
            JSONObject jsonResponse = new JSONObject(responseBody);

            String responseStatus = jsonResponse.getJSONObject("response").getString("response_status");
            if ("success".equals(responseStatus)) {
                var paymentUrl = jsonResponse.getJSONObject("response").getString("checkout_url");
                log.info(paymentUrl);
                return paymentUrl;
            } else {
                String errorMessage = jsonResponse.getJSONObject("response").getString("error_message");
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } else {
            String errorMessage = "Empty response received.";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private Map<String, Object> getPayParameters(Order order) {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", order.getId());
        params.put("merchant_id", merchantId);
        params.put("order_desc", generateOrderDescription(order));

        BigDecimal total = order.getTotal();
        BigDecimal totalInCents = total.multiply(BigDecimal.valueOf(100));
        params.put("amount", totalInCents.intValue());
        params.put("currency", order.getCurrency().getCode());
        params.put("response_url", frontendUrl + "/order/" + order.getToken());
        params.put("server_callback_url", backendUrl + "/api/payments/fondy-callback");
        params.put("sender_email", order.getEmail());
        params.put(SIGNATURE_KEY, generateSignature(params));

        return params;
    }

    private String generateSignature(Map<String, Object> parameters) {
        Map<String, Object> sortedParams = new TreeMap<>(parameters);

        StringJoiner joiner = new StringJoiner("|");
        joiner.add(merchantPassword);
        sortedParams.forEach((key, value) -> {
            if (!ObjectUtils.isEmpty(value)) {
                if (ObjectUtils.isEmpty(value) || SIGNATURE_KEY.equals(key) || RESPONSE_SIGNATURE_STRING_KEY.equals(key)) {
                    return;
                }
                joiner.add(value.toString());
            }
        });

        return DigestUtils.sha1Hex(joiner.toString());
    }

    @SuppressWarnings("unused")
    public boolean isSignaturesEquals(Map<String, String> parameters) {
        String signature1 = parameters.get(SIGNATURE_KEY);
        String signature2 = generateSignature(new TreeMap<>(parameters));
        return signature1.equals(signature2);
    }

    private String generateOrderDescription(Order order) {
        return "Payment for order #" + order.getId();
    }
}
