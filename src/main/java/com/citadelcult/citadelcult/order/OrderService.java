package com.citadelcult.citadelcult.order;

import com.citadelcult.citadelcult.cart.CartService;
import com.citadelcult.citadelcult.cart.entities.Cart;
import com.citadelcult.citadelcult.cart.entities.CartLineItem;
import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import com.citadelcult.citadelcult.order.entities.LineItem;
import com.citadelcult.citadelcult.order.entities.Order;
import com.citadelcult.citadelcult.order.events.PlaceOrderEvent;
import com.citadelcult.citadelcult.product.entities.Product;
import com.citadelcult.citadelcult.product.entities.ProductVariant;
import com.citadelcult.citadelcult.user.User;
import com.citadelcult.citadelcult.user.UserRole;
import com.citadelcult.citadelcult.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final Environment environment;

    @Transactional
    public Order placeOrder(Long cartId) {
        var cart = cartService.completeCart(cartId);

        // Check for items in the shopping cart
        if (cart.getLineItems() == null || cart.getLineItems().isEmpty()) {
            String errorMessage = "Cart with id " + cartId + " has no items";
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        // Check for shipping address
        if (cart.getShippingAddress() == null || cart.getShippingAddress().getPhone() == null ||
                cart.getShippingAddress().getFirstName() == null || cart.getShippingAddress().getLastName() == null || cart.getShippingAddress().getCountry() == null) {
            String errorMessage = "Cart with id " + cartId + " has invalid shipping address";
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        // Checking for a shipping method
        if (cart.getShippingMethod() == null) {
            String errorMessage = "Cart with id " + cartId + " has no shipping method";
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        // Checking for a payment method
        if (cart.getPaymentProvider() == null) {
            String errorMessage = "Cart with id " + cartId + " has no payment provider";
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        // Checking for market availability
        if (cart.getMarket() == null) {
            String errorMessage = "Cart with id " + cartId + " has no market";
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        Order order = new Order();
        order.setEmail(cart.getEmail());
        order.setExternalId(generateExternalId());
        order.setMarket(cart.getMarket());
        order.setPaymentProvider(cart.getPaymentProvider());
        order.setCart(cart);

        var customer = userService.getUserByEmail(cart.getEmail());
        if (customer == null) {
            var newCustomer = new User();
            newCustomer.setEmail(cart.getEmail());
            newCustomer.setRole(UserRole.ROLE_CUSTOMER);
            if (cart.getShippingMethod() != null) {
                newCustomer.setPhone(cart.getShippingAddress().getPhone());
                newCustomer.setFirstName(cart.getShippingAddress().getFirstName());
                newCustomer.setLastName(cart.getShippingAddress().getLastName());
            }
            customer = userService.createUser(newCustomer);
        }
        order.setUser(customer);

        order.setShippingAddress(cart.getShippingAddress());
        order.setBillingAddress(cart.getBillingAddress());
        order.setShippingMethod(cart.getShippingMethod());
        order.setShippingPrice(cart.getShippingPrice());
        order.setCurrency(cart.getMarket().getCurrency());

        // Convert CartLineItem to LineItem and add them to an order
        List<LineItem> lineItems = new ArrayList<>();
        for (CartLineItem cartLineItem : cart.getLineItems()) {
            LineItem lineItem = getLineItem(cartLineItem, cart, order);

            lineItems.add(lineItem);
        }
        order.setLineItems(lineItems);

        var createdOrder = orderRepository.save(order);

        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            eventPublisher.publishEvent(new PlaceOrderEvent(this, createdOrder.getId()));
        }
        return createdOrder;
    }

    @NotNull
    private static LineItem getLineItem(CartLineItem cartLineItem, Cart cart, Order order) {
        LineItem lineItem = new LineItem();
        ProductVariant variant = cartLineItem.getVariant();
        Product product = variant.getProduct();

        lineItem.setName(product.getName());
        lineItem.setDescription(product.getDescription());
        lineItem.setThumbnail(product.getThumbnail());
        lineItem.setPrice(variant.getPriceByCurrencyCode(cart.getMarket().getCurrency().getCode()).getPrice());
        lineItem.setSalePrice(variant.getPriceByCurrencyCode(cart.getMarket().getCurrency().getCode()).getSalePrice());
        lineItem.setQuantity(cartLineItem.getQuantity());
        lineItem.setVariant(variant);
        lineItem.setVariantName(variant.getName());
        lineItem.setProduct(variant.getProduct());
        lineItem.setOrder(order);
        return lineItem;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Order findByToken(String token) {
        return orderRepository.findByToken(token);
    }

    public Order findByTokenOrThrow(String token) {
        Order order = findByToken(token);
        if (order == null) {
            String errorMessage = "Order with token " + token + " not found";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }
        return order;
    }

    private String generateExternalId() {
        long orderCount = orderRepository.count() + 1001;
        String format = "%04d";
        return String.format("#" + format, orderCount);
    }
}
