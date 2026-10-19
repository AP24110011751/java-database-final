package com.project.code.Model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import java.util.*;

@Entity
public class Customer {

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)    
   private Long id;

  @NotNull(message="name cannot be empty")
  private String name;
  
  @NotNull(message="Email cannot be null")
  private String email;
  
  @NotNull(message="Phone cannot be null")
  private String phone;
  
  @OneToMany(mappedBy="customer",fetch=FetchType.EAGER)
  @JsonManagedReference
  private List<OrderDetails> orders;
  
  public Customer(){}
  public void setId(Long id){
    this.id=id;
  }
  public Long getId() {
     return id;
  }
  public void setName(String name){
    this.name=name;
  }
  public String getName(){
    return name;
  }
  public void setEmail(String email){
    this.email=email;
  }
  public String getEmail(){
    return email;
  }
  public void setPhone(String phone){
    this.phone=phone;
  }
  public String getPhone(){
    return phone;
  }

}

