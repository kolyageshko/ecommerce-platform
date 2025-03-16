package com.citadelcult.citadelcult.facebook;

import com.citadelcult.citadelcult.order.OrderService;
import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.serverside.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversionApiService {

    private final OrderService orderService;

    @Value("${facebook.access-token}")
    private String ACCESS_TOKEN;

    @Value("${facebook.pixel-id}")
    public String PIXEL_ID;

    @SuppressWarnings("unused")
    public void placeOrderEvent(Long orderId) {
        var order = orderService.findById(orderId);
        APIContext context = new APIContext(ACCESS_TOKEN);

        UserData userData = new UserData();
        userData.setEmails(Collections.singletonList(order.getEmail()));
        userData.setPhones(Collections.singletonList(order.getShippingAddress().getPhone()));

        var cart = order.getCart();
        if (cart != null) {
            var cartContext = cart.getContext();
            if (cartContext != null) {
                userData.setClientIpAddress(cartContext.getIp());
                userData.setClientUserAgent(cartContext.getUserAgent());
                userData.setFbc(cartContext.getFbc());
                userData.setFbp(cartContext.getFbp());
            }
        }

        List<Content> contents = order.getLineItems().stream()
                .map(lineItem -> new Content()
                        .productId(lineItem.getProduct().getId().toString())
                        .quantity((long) lineItem.getQuantity())
                        .itemPrice(lineItem.getPrice().floatValue())
                        .deliveryCategory(DeliveryCategory.home_delivery))
                .toList();

        CustomData customData = new CustomData();
        customData.setContents(contents);
        customData.setCurrency(order.getCurrency().getCode().toLowerCase());
        customData.setValue(order.getTotal().floatValue());

        Event purchaseEvent = new Event();
        purchaseEvent.eventName("Purchase")
                .eventTime(System.currentTimeMillis() / 1000L)
                .userData(userData)
                .customData(customData)
                .eventSourceUrl("https://citadelcult.com/product/reflect-t-shirt")
                .actionSource(ActionSource.website);

        EventRequest eventRequest = new EventRequest(PIXEL_ID, context);
        eventRequest.addDataItem(purchaseEvent);

        try {
            eventRequest.execute();
        } catch (APIException e) {
            log.error("Failed to send event to Facebook Conversion API", e);
        }
    }
}
