package com.citadelcult.citadelcult.geoip;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/geo-ip")
@RequiredArgsConstructor
public class GeoIpController {
    private final GeoIpService geoIpService;

    @GetMapping("/{ipAddress}")
    public ResponseEntity<Map<String, String>> getCountryByIp(@PathVariable String ipAddress) {
        String countryCode = geoIpService.getCountryCodeByIp(ipAddress);

        Map<String, String> response = new HashMap<>();
        response.put("country_code", countryCode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
