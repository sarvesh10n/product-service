package com.scaler.capstone.project.product.controller;

import com.scaler.capstone.project.product.dto.FakeStoreProductDTO;
import com.scaler.capstone.project.product.models.Product;
import com.scaler.capstone.project.product.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;
    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/")
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }
    @GetMapping("/{id}")
    public Product getSingleProduct(@PathVariable("id") long id){

        return productService.getSingleProduct(id);
    }

    @PostMapping("/add")
    public FakeStoreProductDTO addNewProduct(@RequestBody FakeStoreProductDTO product){

        return productService.addNewProduct(product);
    }

    @PatchMapping("/{id}")
    public Product updateProduct(@PathVariable("id") long id, @RequestBody Product product){
        // Patch is to change something in existing data
        return new Product();
    }

    @PutMapping("/{id}")
    public Product replaceProduct(@PathVariable("id") long id, @RequestBody Product product){
        // put is used replace whole existing data with new data
        return new Product();
    }
}