package com.citadelcult.citadelcult.product;

import com.citadelcult.citadelcult.currency.CurrenciesService;
import com.citadelcult.citadelcult.currency.entities.Currency;
import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import com.citadelcult.citadelcult.media.MediaService;
import com.citadelcult.citadelcult.media.entities.Media;
import com.citadelcult.citadelcult.product.dtos.CreateProductDTO;
import com.citadelcult.citadelcult.product.dtos.CreateProductVariantDTO;
import com.citadelcult.citadelcult.product.dtos.MoneyAmountDTO;
import com.citadelcult.citadelcult.product.entities.MoneyAmount;
import com.citadelcult.citadelcult.product.entities.Product;
import com.citadelcult.citadelcult.product.entities.ProductVariant;
import com.citadelcult.citadelcult.product.enums.ProductStatus;
import com.citadelcult.citadelcult.store.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final MediaService mediaService;
    private final CurrenciesService currenciesService;
    private final StoreService storeService;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(CreateProductDTO createProductDTO) {
        if (createProductDTO.getName() == null || createProductDTO.getName().isEmpty()) {
            log.error("Name not provided for the product.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name not provided for the product.");
        }

        String handle = generateUniqueHandle(createProductDTO.getHandle());

        Product product = new Product();
        product.setName(createProductDTO.getName());
        product.setHandle(handle);
        product.setStatus(createProductDTO.getStatus());

        if (createProductDTO.getThumbnail() != null && !createProductDTO.getThumbnail().isEmpty()) {
            Media thumbnailMedia = mediaService.getMediaByUrl(createProductDTO.getThumbnail());
            if (thumbnailMedia == null) {
                log.error("Thumbnail not found for URL: " + createProductDTO.getThumbnail());
                throw new ResourceNotFoundException("Thumbnail not found for URL: " + createProductDTO.getThumbnail());
            }
            product.setThumbnail(thumbnailMedia);
        }

        if (createProductDTO.getMedia() != null) {
            List<Media> mediaList = new ArrayList<>();
            for (String mediaUrl : createProductDTO.getMedia()) {
                Media media = mediaService.getMediaByUrl(mediaUrl);
                if (media == null) {
                    log.error("Media not found for URL: " + mediaUrl);
                    throw new ResourceNotFoundException("Media not found for URL: " + mediaUrl);
                }
                mediaList.add(media);
            }
            product.setMedia(mediaList);
        }

        // Save the product to get its ID
        Product savedProduct = productRepository.save(product);

        // Save variants
        if (createProductDTO.getVariants() != null) {
            for (CreateProductVariantDTO createProductVariantDTO : createProductDTO.getVariants()) {
                ProductVariant productVariant = new ProductVariant();
                productVariant.setName(createProductVariantDTO.getName());
                productVariant.setSku(createProductVariantDTO.getSku());
                productVariant.setInventoryStock(createProductVariantDTO.getInventoryStock());
                productVariant.setManageInventory(createProductVariantDTO.getManageInventory());
                productVariant.setAllowBackorder(createProductVariantDTO.getAllowBackorder());
                productVariant.setProduct(savedProduct);

                List<MoneyAmount> prices = new ArrayList<>();
                for (MoneyAmountDTO moneyAmountDTO : createProductVariantDTO.getPrices()) {
                    Currency currency = currenciesService.getByCodeOrThrow(moneyAmountDTO.getCurrencyCode());
                    MoneyAmount moneyAmount = new MoneyAmount();
                    moneyAmount.setCurrency(currency);
                    moneyAmount.setPrice(moneyAmountDTO.getPrice());
                    moneyAmount.setSalePrice(moneyAmountDTO.getSalePrice());
                    moneyAmount.setVariant(productVariant);
                    prices.add(moneyAmount);
                }

                productVariant.setPrices(prices);
                productVariantRepository.save(productVariant);
            }
        }

        return savedProduct;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long productId) {
        var product = getProductByIdOrThrow(productId);
        productRepository.delete(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Product createVariant(Long productId, CreateProductVariantDTO createProductVariantDTO) {
        var product = getProductByIdOrThrow(productId);

        // Retrieve the list of currencies supported by the store
        List<Currency> shopCurrencies = storeService.getStoreCurrencies();
        Set<String> shopCurrencyCodes = shopCurrencies.stream().map(Currency::getCode).collect(Collectors.toSet());

        // Check if the currencies provided in the DTO are supported by the store
        for (MoneyAmountDTO moneyAmountDTO : createProductVariantDTO.getPrices()) {
            String currencyCode = moneyAmountDTO.getCurrencyCode();
            if (!shopCurrencyCodes.contains(currencyCode)) {
                log.error("Currency {} is not supported in the shop.", currencyCode);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Currency " + currencyCode + " is not supported in the shop.");
            }
        }

        // Check for duplicate currency codes in the DTO
        Set<String> uniqueCurrencyCodes = new HashSet<>();
        for (MoneyAmountDTO moneyAmountDTO : createProductVariantDTO.getPrices()) {
            String currencyCode = moneyAmountDTO.getCurrencyCode();
            if (!uniqueCurrencyCodes.add(currencyCode)) {
                log.error("Duplicate currency found in DTO: {}", currencyCode);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate currency found: " + currencyCode);
            }
        }

        ProductVariant productVariant = new ProductVariant();
        productVariant.setName(createProductVariantDTO.getName());
        productVariant.setSku(createProductVariantDTO.getSku());
        productVariant.setInventoryStock(createProductVariantDTO.getInventoryStock());
        productVariant.setManageInventory(createProductVariantDTO.getManageInventory());
        productVariant.setAllowBackorder(createProductVariantDTO.getAllowBackorder());

        productVariant.setProduct(product);

        product.getVariants().add(productVariant);

        productVariant.setPrices(new ArrayList<>());
        // Iterate through the prices provided in the DTO and create corresponding MoneyAmount entities
        for (MoneyAmountDTO moneyAmountDTO : createProductVariantDTO.getPrices()) {
            Currency currency = currenciesService.getByCodeOrThrow(moneyAmountDTO.getCurrencyCode());
            // Create a new MoneyAmount entity for each price
            MoneyAmount moneyAmount = new MoneyAmount();
            moneyAmount.setCurrency(currency);
            moneyAmount.setPrice(moneyAmountDTO.getPrice());
            moneyAmount.setSalePrice(moneyAmountDTO.getSalePrice());

            // Set the product variant for the price
            moneyAmount.setVariant(productVariant);

            // Add the price to the list of prices for the product variant
            productVariant.getPrices().add(moneyAmount);
        }

        return productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Product updateVariant(Long productId, Long variantId, CreateProductVariantDTO createProductVariantDTO) {
        var product = getProductByIdOrThrow(productId);

        if (product.getVariants() == null) {
            log.error("Product has no variants with product id: {}", productId);
            throw new ResourceNotFoundException("Product has no variants with product id: " + productId);
        }

        var variant = product.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Product variant not found with id: {}", variantId);
                    return new ResourceNotFoundException("Product variant not found with id: " + variantId);
                });

        variant.setName(createProductVariantDTO.getName());
        variant.setSku(createProductVariantDTO.getSku());
        variant.setInventoryStock(createProductVariantDTO.getInventoryStock());
        variant.setManageInventory(createProductVariantDTO.getManageInventory());
        variant.setAllowBackorder(createProductVariantDTO.getAllowBackorder());

        productVariantRepository.save(variant);

        return productRepository.save(product);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Product deleteVariant(Long productId, Long variantId) {
        Product product = getProductByIdOrThrow(productId);

        var variant = product.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Product variant not found with id: {}", variantId);
                    return new ResourceNotFoundException("Product variant not found with id: " + variantId);
                });

        product.getVariants().remove(variant);

        variant.setProduct(null);

        productVariantRepository.delete(variant);

        return productRepository.save(product);
    }

    public List<Product> getAllPublishedProducts() {
        return productRepository.findByStatus(ProductStatus.PUBLISHED);
    }

    public Product getProductByHandle(String handle) {
        return productRepository.findByHandle(handle);
    }

    public List<Product> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    @SuppressWarnings("unused")
    public List<Product> getRelatedProducts(Long productId, Long cartId, int limit) {
        Product product;
        if (productId != null) {
            product = getProductById(productId);
        } else {
            product = null;
        }

        List<Product> allPublishedProducts = getProductsByStatus(ProductStatus.PUBLISHED);

        if (product != null) {
            allPublishedProducts = allPublishedProducts
                    .stream()
                    .filter(p -> !p.getId().equals(product.getId()))
                    .collect(Collectors.toList());
        }

        return allPublishedProducts.stream().limit(limit).toList();
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public Product getProductByIdOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    public ProductVariant getProductVariantByIdOrThrow(Long variantId) {
        Optional<ProductVariant> variant = productVariantRepository.findById(variantId);

        return variant.orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + variantId));
    }

    private String generateUniqueHandle(String providedHandle) {
        if (StringUtils.isBlank(providedHandle)) {
            return UUID.randomUUID().toString();
        }

        Product existingProduct = productRepository.findByHandle(providedHandle);
        if (existingProduct == null) {
            String slug = providedHandle.trim().replaceAll("\\s+", "-");
            slug = StringUtils.stripAccents(slug);
            slug = slug.replaceAll("[^a-zA-Z0-9-]", "");
            return slug.toLowerCase();
        }

        return UUID.randomUUID().toString();
    }

    public List<Product> searchProducts(String q, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findByNameContainingIgnoreCase(q, pageable);
    }
}
