
package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public ServiceClass(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    // 1. Check whether inventory exists
    public boolean validateInventory(Inventory inventory) {
        return !inventoryRepository
                .existsByProduct_IdAndStore_Id(
                        inventory.getProduct().getId(),
                        inventory.getStore().getId());
    }

    // 2. Check whether a product with the same name exists
    public boolean validateProduct(Product product) {
        return !productRepository.existsByName(product.getName());
    }

    // 3. Check whether a product ID exists
    public boolean ValidateProductId(long id) {
        return productRepository.existsById(id);
    }

    // 4. Get inventory for a product and store
    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProduct_IdAndStore_Id(
                inventory.getProduct().getId(),
                inventory.getStore().getId());
    }
}
