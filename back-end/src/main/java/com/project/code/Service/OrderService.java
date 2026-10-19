package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(
            OrderDetailsRepository orderDetailsRepository,
            CustomerRepository customerRepository,
            StoreRepository storeRepository,
            InventoryRepository inventoryRepository,
            OrderItemRepository orderItemRepository) {

        this.orderDetailsRepository = orderDetailsRepository;
        this.customerRepository = customerRepository;
        this.storeRepository = storeRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public void saveOrder(PlaceOrderRequestDTO request) {

        // Validate request
        if (request == null || request.getStoreId() == null) {
            throw new RuntimeException("Invalid order request or store ID");
        }

        // Find existing customer by email
        Customer customer =
                customerRepository.findByEmail(request.getCustomerEmail());

        // Create customer if not found
        if (customer == null) {
            customer = new Customer();
            customer.setName(request.getCustomerName());
            customer.setEmail(request.getCustomerEmail());
            customer.setPhone(request.getCustomerPhone());

            customer = customerRepository.save(customer);
        }

        // Find store
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() ->
                        new RuntimeException("Store not found"));

        // Get purchased products
        List<PurchaseProductDTO> items = request.getPurchaseProduct();

        if (items == null || items.isEmpty()) {
            throw new RuntimeException(
                    "Order must contain at least one product");
        }

        double totalPrice = 0.0;

        // Validate stock and calculate order total
        for (PurchaseProductDTO item : items) {

            if (item.getId() == null) {
                throw new RuntimeException("Product ID is required");
            }

            if (item.getQuantity() == null ||
                    item.getQuantity() <= 0) {
                throw new RuntimeException(
                        "Quantity must be greater than zero");
            }

            Inventory inventory =
                    inventoryRepository.findByProduct_IdAndStore_Id(
                            item.getId(), store.getId());

            if (inventory == null) {
                throw new RuntimeException(
                        "Inventory not found for product ID: "
                                + item.getId());
            }

            if (inventory.getStockLevel() == null ||
                    inventory.getStockLevel() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + inventory.getProduct().getName());
            }

            totalPrice += inventory.getProduct().getPrice()
                    * item.getQuantity();
        }

        // Save order details
        OrderDetails order = new OrderDetails(
                customer,
                store,
                totalPrice,
                LocalDateTime.now());

        order = orderDetailsRepository.save(order);

        // Reduce stock and save order items
        for (PurchaseProductDTO item : items) {

            Inventory inventory =
                    inventoryRepository.findByProduct_IdAndStore_Id(
                            item.getId(), store.getId());

            inventory.setStockLevel(
                    inventory.getStockLevel() - item.getQuantity());

            inventoryRepository.save(inventory);

            OrderItem orderItem = new OrderItem(
                    order,
                    inventory.getProduct(),
                    item.getQuantity(),
                    inventory.getProduct().getPrice());

            orderItemRepository.save(orderItem);
        }
    }
}