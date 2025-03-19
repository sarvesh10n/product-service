package com.scaler.capstone.product.services;

import com.scaler.capstone.product.exceptions.ProductNotExistException;
import com.scaler.capstone.product.models.Product;

import java.util.List;

public interface ProductService {

    Product getSingleProduct(Long id) throws ProductNotExistException;

    List<Product> getAllProducts();

    Product updateProduct(Long id, Product product);

    Product replaceProduct(Long id, Product product);

    Product addNewProduct(Product product);

    boolean deleteProduct(Long id);
}
