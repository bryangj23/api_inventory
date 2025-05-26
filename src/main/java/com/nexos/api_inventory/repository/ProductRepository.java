package com.nexos.api_inventory.repository;

import com.nexos.api_inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Boolean existsByName(String name);

    Boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT COUNT(*) > 0 FROM ProductMovement WHERE product.id = :id")
    Boolean existsProductsById(Long id);

}
