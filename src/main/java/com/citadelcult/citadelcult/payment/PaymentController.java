package com.citadelcult.citadelcult.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    @PostMapping("/fondy-callback")
    public ResponseEntity<String> fondyCallback(@RequestParam Map<String, String> response) {
        log.info("Fondy Data: {}", response);

//        if (!fondy.isSignaturesEquals(response)) {
//            var errorMessage = "Signatures do not match";
//            log.error(errorMessage);
//            throw new RuntimeException(errorMessage);
//        }

        String data = response.toString();
        String orderNumber = response.get("order_id");
        String orderStatus = response.get("order_status");
        log.info("Data: {}", data);
        log.info("Order Number: {}", orderNumber);
        log.info("Order Status: {}", orderStatus);

        return ResponseEntity.ok().build();
    }
}
