
package com.project.code.Controller;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import com.project.code.Service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    // 1. Add a new store
    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {

        Map<String, String> response = new HashMap<>();

        storeRepository.save(store);

        response.put("message", "Store created successfully");

        return response;
    }

    // 2. Validate whether a store exists
    @GetMapping("/validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {

        return storeRepository.existsById(storeId);
    }

    // 3. Place an order
    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(
            @RequestBody PlaceOrderRequestDTO request) {

        Map<String, String> response = new HashMap<>();

        try {
            orderService.saveOrder(request);
            response.put("message", "Order placed successfully");

        } catch (Exception e) {
            response.put("Error", "Unable to place order: " + e.getMessage());
        }

        return response;
    }
}
