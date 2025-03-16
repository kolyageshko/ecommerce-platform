package com.citadelcult.citadelcult.geoip;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoIpService {

    private final DatabaseReader databaseReader;

    public String getCountryCodeByIp(String ip)  {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            CountryResponse countryResponse = databaseReader.country(inetAddress);
            return countryResponse.getCountry().getIsoCode();
        } catch (Exception e) {
            return null;
        }
    }

    public String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        }
        return xForwardedForHeader.split(",")[0];
    }
}
