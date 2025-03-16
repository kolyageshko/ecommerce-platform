package com.citadelcult.citadelcult.notification;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate;

    public void sendMessage(String message) {
        String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"chat_id\":\"" + chatId + "\",\"text\":\"" + message + "\"}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
    }
}
