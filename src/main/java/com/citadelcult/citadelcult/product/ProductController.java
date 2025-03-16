package com.citadelcult.citadelcult.product;

import com.citadelcult.citadelcult.product.dtos.CreateProductDTO;
import com.citadelcult.citadelcult.product.dtos.CreateProductVariantDTO;
import com.citadelcult.citadelcult.product.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllPublishedProducts() {
        List<Product> products = productService.getAllPublishedProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductByIdOrThrow(productId);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/handle/{handle}")
    public ResponseEntity<Product> getProductByHandle(@PathVariable String handle) {
        Product product = productService.getProductByHandle(handle);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public Product createProduct(@RequestBody CreateProductDTO createProductDTO) {
        return productService.createProduct(createProductDTO);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/variants")
    public Product createVariant(
            @PathVariable Long productId,
            @Validated @RequestBody CreateProductVariantDTO createProductVariantDTO
    ) {
        return productService.createVariant(productId, createProductVariantDTO);
    }

    @PutMapping("/{productId}/variants/{variantId}")
    public Product updateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Validated @RequestBody CreateProductVariantDTO createProductVariantDTO
            ) {
        return productService.updateVariant(productId, variantId, createProductVariantDTO);
    }

    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<Product> deleteVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId
    ) {
        var product = productService.deleteVariant(productId, variantId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/related")
    public List<Product> getRelatedProducts(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false) Long cartId
    ) {
        return productService.getRelatedProducts(productId, cartId, limit);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        List<Product> products = productService.searchProducts(q, limit);
        return ResponseEntity.ok(products);
    }
}
