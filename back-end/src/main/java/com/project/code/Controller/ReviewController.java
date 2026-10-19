
package com.project.code.Controller;

import com.project.code.Model.*;
import com.project.code.Repo.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Get reviews for a specific store and product
    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> reviewsList = new ArrayList<>();

        List<Review> reviews =
                reviewRepository.findByStoreIdAndProductId(storeId, productId);

        for (Review review : reviews) {

            Map<String, Object> reviewData = new HashMap<>();

            reviewData.put("comment", review.getComment());
            reviewData.put("rating", review.getRating());

            String customerName = "Unknown";

            Optional<Customer> customer =
                    customerRepository.findById(review.getCustomerId());

            if (customer.isPresent()) {
                customerName = customer.get().getName();
            }

            reviewData.put("customerName", customerName);
            reviewsList.add(reviewData);
        }

        response.put("reviews", reviewsList);

        return response;
    }
}
