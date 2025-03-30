package com.scaler.capstone.product.controllers;

import com.scaler.capstone.product.dto.CreateProductDTO;
import com.scaler.capstone.product.dto.ProductDTO;
import com.scaler.capstone.product.exceptions.CategoryNotExistException;
import com.scaler.capstone.product.exceptions.InvalidDataException;
import com.scaler.capstone.product.exceptions.ProductNotExistException;
import com.scaler.capstone.product.exceptions.ResourceAccessForbiddenException;
import com.scaler.capstone.product.models.User;
import com.scaler.capstone.product.models.product.Product;
import com.scaler.capstone.product.repositories.UserRepository;
import com.scaler.capstone.product.services.ProductService;
import com.scaler.capstone.product.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("products")
public class ProductController {

    private ProductService productService;
    private UserRepository userRepository;
    public ProductController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create-product")
    public ResponseEntity<ProductDTO> createProduct(Authentication authentication,
                                                   @Valid @RequestBody CreateProductDTO createProductDTO)
            throws InvalidDataException, ResourceAccessForbiddenException,CategoryNotExistException {

        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);

        if(!(user.getRoles().contains("ADMIN")) && (!user.getRoles().contains("SUPER_ADMIN")) ) {
            throw new ResourceAccessForbiddenException("No Access to create product");
        }
        Product product = productService.createProduct(createProductDTO);
        return new ResponseEntity<>(ProductDTO.fromProduct(product), HttpStatus.CREATED);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProduct(Authentication authentication, @PathVariable long id) throws ProductNotExistException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        UserUtils.createUserIfNotExist(jwt, userRepository);

        Product product = productService.getProductById(id);
        return new ResponseEntity<>(ProductDTO.fromProduct(product), HttpStatus.OK);
    }

    @GetMapping("/product/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDtoList = new ArrayList<>();
        for (Product product : products) {
            productDtoList.add(ProductDTO.fromProduct(product));
        }
        return new ResponseEntity<>(productDtoList, HttpStatus.OK);
    }

    @PatchMapping("/update-product/{id}")
    public ResponseEntity<ProductDTO> updateProduct(Authentication authentication, @PathVariable long id,
                                                    @RequestBody Map<String, Object> updates) throws ResourceAccessForbiddenException, ProductNotExistException, CategoryNotExistException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);

        if(!(user.getRoles().contains("ADMIN")) && (!user.getRoles().contains("SUPER_ADMIN")) ) {
            throw new ResourceAccessForbiddenException("Not Access to Update product");
        }
        Product product = productService.updateProduct(id, updates);
        return new ResponseEntity<>(ProductDTO.fromProduct(product), HttpStatus.OK);
    }
}