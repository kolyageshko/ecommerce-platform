package com.citadelcult.citadelcult.order.listeners;

import com.citadelcult.citadelcult.notification.MessageFormatter;
import com.citadelcult.citadelcult.notification.TelegramService;
import com.citadelcult.citadelcult.order.events.PlaceOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PlaceOrderListener {

    private final TelegramService telegramService;
    private final MessageFormatter messageFormatter;

    @Async
    @TransactionalEventListener()
    public void handlePlaceOrderEvent(PlaceOrderEvent event) {
        String message = messageFormatter.formatOrderMessage(event.getOrderId());
        telegramService.sendMessage(message);

//        conversionApiService.placeOrderEvent(event.getOrderId());
    }
}
