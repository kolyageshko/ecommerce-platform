package com.citadelcult.citadelcult.cart;

import com.citadelcult.citadelcult.cart.dtos.AddLineItemDTO;
import com.citadelcult.citadelcult.cart.dtos.CreateCartDTO;
import com.citadelcult.citadelcult.cart.dtos.UpdateCartDTO;
import com.citadelcult.citadelcult.cart.dtos.UpdateLineItemDTO;
import com.citadelcult.citadelcult.cart.entities.Cart;
import com.citadelcult.citadelcult.cart.entities.CartLineItem;
import com.citadelcult.citadelcult.cart.entities.Context;
import com.citadelcult.citadelcult.country.CountriesService;
import com.citadelcult.citadelcult.country.entities.Country;
import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import com.citadelcult.citadelcult.geoip.GeoIpService;
import com.citadelcult.citadelcult.market.MarketsService;
import com.citadelcult.citadelcult.market.entities.Market;
import com.citadelcult.citadelcult.order.entities.Address;
import com.citadelcult.citadelcult.payment.PaymentService;
import com.citadelcult.citadelcult.product.ProductService;
import com.citadelcult.citadelcult.product.entities.MoneyAmount;
import com.citadelcult.citadelcult.product.entities.ProductVariant;
import com.citadelcult.citadelcult.shipping.ShippingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final GeoIpService geoIpService;
    private final MarketsService marketsService;
    private final ProductService productService;
    private final CountriesService countriesService;
    private final ShippingService shippingService;
    private final PaymentService paymentService;
    private final CartLineItemRepository cartLineItemRepository;

    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId).filter(cart -> cart.getCompletedAt() == null).orElse(null);
    }

    public Cart getCartOrThrow(Long cartId) {
        var cart = getCart(cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found with id: " + cartId);
        }

        return cart;
    }

    public Cart completeCart(Long cartId) {
        var cart = getCartOrThrow(cartId);
        cart.setCompletedAt(Instant.now());
        return cartRepository.save(cart);
    }

    @SneakyThrows
    public Cart createCart(HttpServletRequest request) {
        Cart newCart = new Cart();

        String ipAddress = geoIpService.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        Context context = new Context();
        context.setIp(ipAddress);
        context.setUserAgent(userAgent);
        newCart.setContext(context);

        Market market = marketsService.getDefaultMarket();
        newCart.setMarket(market);
        return cartRepository.save(newCart);
    }

    @SneakyThrows
    public Cart createCart(CreateCartDTO createCartDTO, HttpServletRequest request) {
        Cart newCart = new Cart();

        String ipAddress = geoIpService.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        Context context = new Context();
        context.setIp(ipAddress);
        context.setUserAgent(userAgent);
        newCart.setContext(context);

        String countryCode = createCartDTO.getCountryCode().toUpperCase();

        Market market = marketsService
                .getMarketByCountryCode(countryCode)
                .orElse(marketsService.getDefaultMarket());

        Country country = countriesService.getByIsoOrThrow(countryCode);
        Address shippingAddress = new Address();
        shippingAddress.setCountry(country);

        newCart.setMarket(market);
        newCart.setShippingAddress(shippingAddress);
        return cartRepository.save(newCart);
    }

    public Cart getOrCreateCart(Long cartId, HttpServletRequest request) {
        Cart cart = getCart(cartId);
        if (cart == null) {
            cart = createCart(request);
        }

        return cart;
    }

    public Cart updateCart(Long cartId, UpdateCartDTO updateCartDTO) {
        var cart = getCartOrThrow(cartId);
        var shippingAddress = cart.getShippingAddress();
        if (shippingAddress == null) {
            shippingAddress = new Address();
            cart.setShippingAddress(shippingAddress);
        }

        if (updateCartDTO.getEmail() != null) {
            cart.setEmail(updateCartDTO.getEmail());
        }

        if (updateCartDTO.getShippingAddressDTO() != null) {
            var shippingAddressDTO = updateCartDTO.getShippingAddressDTO();
            if (shippingAddressDTO.getCountryCode() != null) {
                String countryCode = shippingAddressDTO.getCountryCode().toUpperCase();
                var country = countriesService.getByIsoOrThrow(countryCode);
                var newMarket = marketsService.getMarketByCountryCode(countryCode).orElse(null);
                if (newMarket == null) {
                    String errorMessage = "Market not found for country code: " + countryCode;
                    log.error(errorMessage);
                    throw new ResourceNotFoundException(errorMessage);
                }

                // Set new market
                cart.setMarket(newMarket);

                // Clear payment provider if not compatible with new market
                var newPaymentProvider = cart.getPaymentProvider();
                if (newPaymentProvider != null && !newMarket.getPaymentProviders().contains(newPaymentProvider)) {
                    cart.setPaymentProvider(null);
                }

                // Clear shipping method
                cart.setShippingMethod(null);

                // Set shipping address country
                shippingAddress.setCountry(country);
            }

            if (shippingAddressDTO.getFirstName() != null) {
                shippingAddress.setFirstName(shippingAddressDTO.getFirstName());
            }

            if (shippingAddressDTO.getLastName() != null) {
                shippingAddress.setLastName(shippingAddressDTO.getLastName());
            }

            if (shippingAddressDTO.getProvince() != null) {
                shippingAddress.setProvince(shippingAddressDTO.getProvince());
            }

            if (shippingAddressDTO.getCity() != null) {
                shippingAddress.setCity(shippingAddressDTO.getCity());
            }

            if (shippingAddressDTO.getAddress1() != null) {
                shippingAddress.setAddress1(shippingAddressDTO.getAddress1());
            }

            if (shippingAddressDTO.getAddress2() != null) {
                shippingAddress.setAddress2(shippingAddressDTO.getAddress2());
            }

            if (shippingAddressDTO.getPhone() != null) {
                shippingAddress.setPhone(shippingAddressDTO.getPhone());
            }

            if (shippingAddressDTO.getPostalCode() != null) {
                shippingAddress.setPostalCode(shippingAddressDTO.getPostalCode());
            }
        }

        if (updateCartDTO.getShippingMethodId() != null) {
            var sm = shippingService.findById(updateCartDTO.getShippingMethodId());
            if (sm == null) {
                String errorMessage = "Market not found with id: " + updateCartDTO.getShippingMethodId();
                log.error(errorMessage);
                throw new ResourceNotFoundException(errorMessage);
            }
            cart.setShippingMethod(sm);
        }

        if (updateCartDTO.getPaymentProviderId() != null) {
            var pp = paymentService.findPaymentProviderById(updateCartDTO.getPaymentProviderId());
            if (pp == null) {
                String errorMessage = "Payment provider not found with id: " + updateCartDTO.getPaymentProviderId();
                log.error(errorMessage);
                throw new ResourceNotFoundException(errorMessage);
            }
            cart.setPaymentProvider(pp);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addLineItem(Long cartId, AddLineItemDTO addLineItemDTO, HttpServletRequest request) {
        Cart cart = getOrCreateCart(cartId, request);
        Market market = cart.getMarket();
        Long variantId = addLineItemDTO.getVariantId();
        int quantity = addLineItemDTO.getQuantity();
        ProductVariant variant = productService.getProductVariantByIdOrThrow(variantId);

        MoneyAmount price = variant.getPriceByCurrencyCode(market.getCurrency().getCode());
        if (price == null) {
            String marketCurrency = market.getCurrency().getCode();
            String errorMessage = String.format("Price for variant %s in market currency %s not found", variantId, marketCurrency);
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }

        if (!variant.isAvailable()) {
            String errorMessage = "Product variant is not available to add to cart with ID: " + variantId;
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        int existingQuantityInCart = 0;
        CartLineItem lineItem = null;
        List<CartLineItem> items = cart.getLineItems();
        if (items != null) {
            for (CartLineItem item : items) {
                if (variantId.equals(item.getVariant().getId())) {
                    lineItem = item;
                    existingQuantityInCart = item.getQuantity();
                    break;
                }
            }
        }

        int totalRequestedQuantity = quantity + existingQuantityInCart;
        if (totalRequestedQuantity > variant.getInventoryStock()) {
            if (!variant.getAllowBackorder()) {
                String errorMessage = "Adding this item to this cart will exceed the available inventory";
                log.error(errorMessage);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
        }

        if (lineItem == null) {
            var newLineItem = new CartLineItem();
            newLineItem.setVariant(variant);
            newLineItem.setQuantity(quantity);
            newLineItem.setCart(cart);

            if (cart.getLineItems() == null) {
                cart.setLineItems(new ArrayList<>());
            }

            cart.getLineItems().add(newLineItem);
        } else {
            if (totalRequestedQuantity <= 0) {
                cart.getLineItems().remove(lineItem);
            } else {
                lineItem.setQuantity(totalRequestedQuantity);
            }
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateLineItem(Long cartId, Long lineId, UpdateLineItemDTO updateLineItemDTO) {
        var cart = getCartOrThrow(cartId);

        CartLineItem lineItemToUpdate = cart.getLineItems().stream()
                .filter(item -> item.getId().equals(lineId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Line item not found in cart with id: " + lineId));

        int requestedQuantity = updateLineItemDTO.getQuantity();
        ProductVariant variant = lineItemToUpdate.getVariant();
        int existingQuantityInCart = lineItemToUpdate.getQuantity();
        int inventoryStock = variant.getInventoryStock();

        // Check if the requested quantity exceeds available inventory and backorder is not allowed
        if (requestedQuantity > inventoryStock + existingQuantityInCart && !variant.getAllowBackorder()) {
            String errorMessage = "Updating the item quantity exceeds the available inventory and backorders are not allowed";
            log.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        lineItemToUpdate.setQuantity(requestedQuantity);

        if (lineItemToUpdate.getQuantity() <= 0) {
            cart.getLineItems().remove(lineItemToUpdate);
        }

        return cartRepository.save(cart);
    }

    public Cart deleteLineItem(Long cartId, Long lineId) {
        var cart = getCartOrThrow(cartId);

        var items = cart.getLineItems();
        if (items == null || items.isEmpty()) {
            String errorMessage = "Cart is empty";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        var lineItem = items.stream()
                .filter(item -> lineId.equals(item.getId()))
                .findFirst()
                .orElse(null);

        if (lineItem == null) {
            String errorMessage = "Line item not found in cart with id: " + lineId;
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        items.remove(lineItem);
        cartLineItemRepository.delete(lineItem);

        return cartRepository.save(cart);
    }

    public Cart findByToken(String token) {
        return cartRepository.findByToken(token);
    }

    public Cart findByTokenOrThrow(String token) {
        Cart cart = findByToken(token);
        if (cart == null) {
            String errorMessage = "Cart with token " + token + " not found";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }
        return cart;
    }
}
