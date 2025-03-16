package com.citadelcult.citadelcult.collection;

import com.citadelcult.citadelcult.exceptions.ResourceNotFoundException;
import com.citadelcult.citadelcult.product.ProductRepository;
import com.citadelcult.citadelcult.product.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ProductRepository productRepository;

    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Collection createCollection(Collection collection) {
        return collectionRepository.save(collection);
    }

    public Collection getCollectionByHandle(String handle) {
        return collectionRepository.findByHandle(handle).orElse(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Collection updateCollection(Long id, Collection updatedCollection) {
        if (collectionRepository.existsById(id)) {
            updatedCollection.setId(id);
            return collectionRepository.save(updatedCollection);
        }
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Collection addProducts(Long collectionId, List<Long> productIds) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found with id: " + collectionId));

        List<Product> products = productRepository.findAllById(productIds);

        if (!products.isEmpty()) {
            collection.getProducts().addAll(products);
            return collectionRepository.save(collection);
        }

        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Collection removeProducts(Long collectionId, List<Long> productIds) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found with id: " + collectionId));

        collection.getProducts().removeIf(product -> productIds.contains(product.getId()));
        return collectionRepository.save(collection);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCollection(Long id) {
        collectionRepository.deleteById(id);
    }
}
