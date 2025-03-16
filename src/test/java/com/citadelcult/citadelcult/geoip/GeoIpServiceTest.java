package com.citadelcult.citadelcult.geoip;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GeoIpServiceTest {

    @Autowired
    private GeoIpService geoIpService;

    @Test
    public void testGetCountryCodeByIp_KnownIp() {
        // Arrange
        String ip = "91.90.11.155";

        // Act
        String countryCode = geoIpService.getCountryCodeByIp(ip);

        // Assert
        assertEquals("UA", countryCode);
    }

    @Test
    public void testGetClientIpAddress_WithXForwardedForHeader() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1");

        // Act
        String ipAddress = geoIpService.getClientIpAddress(request);

        // Assert
        assertEquals("192.168.1.1", ipAddress);
    }

    @Test
    public void testGetClientIpAddress_WithoutXForwardedForHeader() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // Act
        String ipAddress = geoIpService.getClientIpAddress(request);

        // Assert
        assertEquals("127.0.0.1", ipAddress);
    }
}
