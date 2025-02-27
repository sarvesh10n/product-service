package com.scaler.capstone.project.product.services;

import com.scaler.capstone.project.product.dto.FakeStoreProductDTO;
import com.scaler.capstone.project.product.exceptions.ProductNotExistException;
import com.scaler.capstone.project.product.models.Product;

import java.util.List;

public interface ProductService {

    Product getSingleProduct(Long id) throws ProductNotExistException;

    FakeStoreProductDTO addNewProduct(FakeStoreProductDTO product);

    List<Product> getAllProducts();
}
