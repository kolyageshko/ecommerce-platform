package com.citadelcult.citadelcult.order.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlaceOrderEvent extends ApplicationEvent {

    private final Long orderId;

    public PlaceOrderEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }
}
