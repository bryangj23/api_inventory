package com.nexos.api_inventory.repository;

import com.nexos.api_inventory.entity.ProductMovement;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductMovementRepository extends CrudRepository<ProductMovement, Long>, JpaSpecificationExecutor<ProductMovement> {

    Optional<ProductMovement> findByIdAndProductId(Long id, Long productId);

}
