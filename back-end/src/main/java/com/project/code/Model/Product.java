package com.project.code.Model;

import jakarta.persistence.*;
import java.util.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.*;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message="Name cannot be null")
    private String name;
    
    @NotNull(message="Category cannot be null")
    private String category;
    
    @NotNull(message="Price cannot be null")
    private Double price; 
    
    @NotNull(message="sku cannot be null")
    private String sku;
    
    @OneToMany(mappedBy="product")
    @JsonManagedReference("inventory-product")
    private List<Inventory> inventory;
    

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getCategory() {
    return category;
}

public void setCategory(String category) {
    this.category = category;
}

public Double getPrice() {
    return price;
}

public void setPrice(Double price) {
    this.price = price;
}

public String getSku() {
    return sku;
}

public void setSku(String sku) {
    this.sku = sku;
}
}


