package com.scaler.capstone.project.services;

import com.scaler.capstone.project.dto.FakeStoreProductDTO;
import com.scaler.capstone.project.exceptions.ProductNotExistException;
import com.scaler.capstone.project.models.Product;

import java.util.List;

public interface ProductService {

    Product getSingleProduct(Long id) throws ProductNotExistException;

    FakeStoreProductDTO addNewProduct(FakeStoreProductDTO product);

    List<Product> getAllProducts();
}
