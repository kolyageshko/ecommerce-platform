package com.citadelcult.citadelcult.collection;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping
    public List<Collection> getAllCollections() {
        return collectionService.getAllCollections();
    }

    @GetMapping("/{handle}")
    public ResponseEntity<Collection> getCollectionByHandle(@PathVariable String handle) {
        var collection = collectionService.getCollectionByHandle(handle);
        return ResponseEntity.ok(collection);
    }

    @PostMapping
    public ResponseEntity<Collection> createCollection(@RequestBody Collection collection) {
        var createdCollection = collectionService.createCollection(collection);
        return ResponseEntity.ok(createdCollection);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(@PathVariable Long id, @RequestBody Collection updatedCollection) {
        var updated = collectionService.updateCollection(id, updatedCollection);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/products/batch")
    public ResponseEntity<Collection> addProducts(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> requestBody
    ) {
        List<Long> productIds = requestBody.get("product_ids");
        var updatedCollection = collectionService.addProducts(id, productIds);
        if (updatedCollection != null) {
            return new ResponseEntity<>(updatedCollection, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/products/batch")
    public ResponseEntity<Collection> removeProducts(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> requestBody
    ) {
        List<Long> productIds = requestBody.get("product_ids");
        Collection updatedCollection = collectionService.removeProducts(id, productIds);
        if (updatedCollection != null) {
            return new ResponseEntity<>(updatedCollection, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }
}
