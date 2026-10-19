
package com.project.code.Controller;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import com.project.code.Service.ServiceClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

    // 1. Add a product
    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {

        Map<String, String> response = new HashMap<>();

        try {
            if (!serviceClass.validateProduct(product)) {
                response.put("message", "Product already exists");
                return response;
            }

            productRepository.save(product);
            response.put("message", "Product added successfully");

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Product SKU already exists");
        } catch (Exception e) {
            response.put("message", "Unable to add product");
        }

        return response;
    }

    // 2. Get product by ID
    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        productRepository.findById(id).ifPresentOrElse(
                product -> response.put("products", product),
                () -> response.put("message", "Product not found")
        );

        return response;
    }

    // 3. Update a product
    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {

        Map<String, String> response = new HashMap<>();

        if (product.getId() == null ||
                !serviceClass.ValidateProductId(product.getId())) {
            response.put("message", "Product not found");
            return response;
        }

        try {
            productRepository.save(product);
            response.put("message", "Product updated successfully");
        } catch (Exception e) {
            response.put("message", "Unable to update product");
        }

        return response;
    }

    // 4. Filter products by name and category
    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(
            @PathVariable String name,
            @PathVariable String category) {

        Map<String, Object> response = new HashMap<>();

        boolean noName = name.equalsIgnoreCase("null");
        boolean noCategory = category.equalsIgnoreCase("null");

        if (noName && noCategory) {
            response.put("products", productRepository.findAll());
        } else if (noName) {
            response.put("products",
                    productRepository.findByCategory(category));
        } else if (noCategory) {
            response.put("products",
                    productRepository.findProductBySubName(name));
        } else {
            response.put("products",
                    productRepository.findProductBySubNameAndCategory(
                            name, category));
        }

        return response;
    }

    // 5. List all products
    @GetMapping
    public Map<String, Object> listProduct() {

        Map<String, Object> response = new HashMap<>();
        response.put("products", productRepository.findAll());

        return response;
    }

    // 6. Get products by category and store ID
    @GetMapping("/filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(
            @PathVariable String category,
            @PathVariable Long storeid) {

        Map<String, Object> response = new HashMap<>();

        response.put("product",
                productRepository.findProductByCategory(category, storeid));

        return response;
    }

    // 7. Delete a product
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {
            response.put("message", "Product not found");
            return response;
        }

        try {
            inventoryRepository.deleteByProduct_Id(id);
            productRepository.deleteById(id);

            response.put("message", "Product deleted successfully");
        } catch (Exception e) {
            response.put("message",
                    "Unable to delete product because it may be used in an order");
        }

        return response;
    }

    // 8. Search products by name
    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {

        Map<String, Object> response = new HashMap<>();

        response.put("products",
                productRepository.findProductBySubName(name));

        return response;
    }
}
