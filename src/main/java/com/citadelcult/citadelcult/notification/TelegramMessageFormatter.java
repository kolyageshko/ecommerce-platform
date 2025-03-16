package com.citadelcult.citadelcult.notification;

import com.citadelcult.citadelcult.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramMessageFormatter implements MessageFormatter {

    private final OrderService orderService;

    @Override
    public String formatOrderMessage(Long orderId) {
        var order = orderService.findById(orderId);
        int itemCount = order.getItemsCount();
        String itemWord = itemCount == 1 ? "item" : "items";

        return "You have a new order for " + itemCount + " " + itemWord + " totaling " + order.getCurrency().getSymbol() + order.getTotal() + " from Online Store.";
    }
}