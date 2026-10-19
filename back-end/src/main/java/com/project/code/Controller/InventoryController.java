
package com.project.code.Controller;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import com.project.code.Service.ServiceClass;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    // 1. Update inventory
    @PutMapping("/update")
    public Map<String, String> updateInventory(
            @RequestBody CombinedRequest request) {

        Map<String, String> response = new HashMap<>();

        Product product = request.getProduct();
        Inventory inventory = request.getInventory();

        if (product == null || product.getId() == null ||
                !serviceClass.ValidateProductId(product.getId())) {
            response.put("message", "Product not found");
            return response;
        }

        Inventory existingInventory =
                serviceClass.getInventoryId(inventory);

        if (existingInventory != null) {
            existingInventory.setStockLevel(inventory.getStockLevel());
            inventoryRepository.save(existingInventory);
            response.put("message", "Inventory updated successfully");
        } else {
            response.put("message", "No inventory data available");
        }

        return response;
    }

    // 2. Save new inventory
    @PostMapping("/save")
    public Map<String, String> saveInventory(
            @RequestBody Inventory inventory) {

        Map<String, String> response = new HashMap<>();

        if (!serviceClass.validateInventory(inventory)) {
            response.put("message", "Inventory already exists");
            return response;
        }

        inventoryRepository.save(inventory);
        response.put("message", "Inventory saved successfully");

        return response;
    }

    // 3. Get all products available in a store
    @GetMapping("/products/{storeId}")
    public Map<String, Object> getAllProducts(
            @PathVariable Long storeId) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "products",
                productRepository.findProductsByStoreId(storeId)
        );

        return response;
    }

    // 4. Filter products by category and name
    @GetMapping("/filter")
    public Map<String, Object> getProductName(
            @RequestParam String category,
            @RequestParam String name) {

        Map<String, Object> response = new HashMap<>();

        boolean noCategory = category.equalsIgnoreCase("null");
        boolean noName = name.equalsIgnoreCase("null");

        if (noCategory && noName) {
            response.put("product", productRepository.findAll());
        } else if (noCategory) {
            response.put(
                    "product",
                    productRepository.findProductBySubName(name)
            );
        } else if (noName) {
            response.put(
                    "product",
                    productRepository.findByCategory(category)
            );
        } else {
            response.put(
                    "product",
                    productRepository.findProductBySubNameAndCategory(
                            name, category)
            );
        }

        return response;
    }

    // 5. Search products by name within a store
    @GetMapping("/search")
    public Map<String, Object> searchProduct(
            @RequestParam String name,
            @RequestParam Long storeId) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "product",
                productRepository.findByNameLike(storeId, name)
        );

        return response;
    }

    // 6. Remove a product
    @DeleteMapping("/remove/{id}")
    public Map<String, String> removeProduct(
            @PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {
            response.put("message", "Product not found");
            return response;
        }

        inventoryRepository.deleteByProduct_Id(id);
        productRepository.deleteById(id);

        response.put("message", "Product deleted successfully");

        return response;
    }

    // 7. Check whether sufficient stock is available
    @GetMapping("/validateQuantity")
    public boolean validateQuantity(
            @RequestParam Long productId,
            @RequestParam Long storeId,
            @RequestParam Integer quantity) {

        if (quantity == null || quantity <= 0) {
            return false;
        }

        Inventory inventory =
                inventoryRepository.findByProduct_IdAndStore_Id(
                        productId, storeId);

        return inventory != null
                && inventory.getStockLevel() != null
                && inventory.getStockLevel() >= quantity;
    }
}
