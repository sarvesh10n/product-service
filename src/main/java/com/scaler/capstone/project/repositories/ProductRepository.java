package com.scaler.capstone.project.repositories;

import com.scaler.capstone.project.models.Category;
import com.scaler.capstone.project.models.Product;
import com.scaler.capstone.project.repositories.projections.ProductWithIdAndTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByTitleContaining(String word);

    long deleteByTitleIgnoreCase(String title);

    List<Product> findByTitleAndDescription(String title,
                                            String description);
    List<Product> findByPriceBetween(double startRange, double endRange);

    List<Product> findByCategory(Category category);

    Product findByIdAndCategoryOrderByTitle(Long id, Category category);

    List<Product> findByCategory_Id(Long id);


    Optional<Product> findById(Long id);

    Product save(Product product);

    @Query("select p.id as id, p.title as title from Product p where p.id = :id")
    List<ProductWithIdAndTitle> somethingsomething(@Param("id") Long id);

    @Query(value = "select p.id as id, p.title as title from product p where p.id = :id", nativeQuery = true)
    List<ProductWithIdAndTitle> somesome2(@Param("id") Long id);
}
