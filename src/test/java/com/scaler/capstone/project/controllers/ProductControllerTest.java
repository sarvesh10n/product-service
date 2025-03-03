package com.scaler.capstone.project.controllers;

import com.scaler.capstone.project.exceptions.ProductNotExistException;
import com.scaler.capstone.project.models.Product;
import com.scaler.capstone.project.repositories.ProductRepository;
import com.scaler.capstone.project.services.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductControllerTest {
    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    void testProductsSameAsService() {
        // arrange
        List<Product> products = new ArrayList<>();
        Product p1 = new Product(); // o
        p1.setTitle("iPhone 15");
        products.add(p1);

        Product p2 = new Product(); // p
        p2.setTitle("iPhone 15 Pro");
        products.add(p2);

        Product p3 = new Product(); // q
        p3.setTitle("iPhone 15 Pro Max");
        products.add(p3);

        List<Product> prodctsToPass = new ArrayList<>();

        for (Product p : products) {
            Product p111 = new Product();
            p111.setTitle(p.getTitle());
            prodctsToPass.add(p111);
        }

        when(
                productService.getAllProducts()
        ).thenReturn(
                prodctsToPass
        );


        // act
        ResponseEntity<List<Product>> response =
                productController.getAllProducts();

        // assert
        List<Product> productsInResponse = response.getBody();

        assertEquals(products.size(), productsInResponse.size());

        for (int i = 0; i < productsInResponse.size(); ++i)
            assertEquals(
                    products.get(i).getTitle(), // o p q
                    productsInResponse.get(i).getTitle()
            );
    }

    @Test
    void testNonExistingProductThrowsException() throws ProductNotExistException {
        // arrange

        when(
                productRepository.findById(10L)
        ).thenReturn(
                Optional.empty()
        );


        when(
                productService.getSingleProduct(10L)
        ).thenReturn(new Product());

        // act
        assertThrows(
                ProductNotExistException.class,
                () -> productController.getSingleProduct(10L)
        );

    }

}