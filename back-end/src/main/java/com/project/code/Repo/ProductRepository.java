
package com.project.code.Repo;

import com.project.code.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find all products - already provided by JpaRepository
    // List<Product> findAll();

    // Find products by category
    public List<Product> findByCategory(String category);

    // Find products within a price range
    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Find products by SKU
    public List<Product> findBySku(String sku);

    // Find a product by exact name
    public Product findByName(String name);

    // Find a product by ID - custom name avoids conflict with JpaRepository
    public Product findProductById(Long id);
    boolean existsByName(String name);

    // Find products by partial name for a specific store
    @Query("SELECT DISTINCT i.product FROM Inventory i " +
           "WHERE i.store.id = :storeId " +
           "AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    public List<Product> findByNameLike(
            @Param("storeId") Long storeId,
            @Param("pname") String pname);

    // Find products by name pattern and category for a specific store
    @Query("SELECT i.product FROM Inventory i " +
           "WHERE i.store.id = :storeId " +
           "AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%')) " +
           "AND i.product.category = :category")
    public List<Product> findByNameAndCategory(
            @Param("storeId") Long storeId,
            @Param("pname") String pname,
            @Param("category") String category);

    // Find products by category for a specific store
    @Query("SELECT i.product FROM Inventory i " +
           "WHERE i.store.id = :storeId " +
           "AND i.product.category = :category")
    public List<Product> findByCategoryAndStoreId(
            @Param("storeId") Long storeId,
            @Param("category") String category);

    // Find products by partial name, ignoring case
    @Query("SELECT i FROM Product i " +
           "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    public List<Product> findProductBySubName(
            @Param("pname") String pname);

    // Find all products for a specific store
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    public List<Product> findProductsByStoreId(
            @Param("storeId") Long storeId);

    // Find products by category and store ID
    @Query("SELECT i.product FROM Inventory i " +
           "WHERE i.product.category = :category " +
           "AND i.store.id = :storeId")
    public List<Product> findProductByCategory(
            @Param("category") String category,
            @Param("storeId") Long storeId);

    // Find products by partial name and category
    @Query("SELECT i FROM Product i " +
           "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) " +
           "AND i.category = :category")
    public List<Product> findProductBySubNameAndCategory(
            @Param("pname") String pname,
            @Param("category") String category);
}
