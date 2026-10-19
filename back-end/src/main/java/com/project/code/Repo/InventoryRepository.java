
package com.project.code.Repo;

import com.project.code.Model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

   public Inventory findByProduct_IdAndStore_Id(Long productId, Long storeId);
   public List<Inventory> findByStore_Id(Long storeId);
   boolean existsByProduct_IdAndStore_Id(Long productId, Long storeId);
    @Modifying
    @Transactional
    void deleteByProduct_Id(Long productId);
}
