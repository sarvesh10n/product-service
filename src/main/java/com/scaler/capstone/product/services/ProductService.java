package com.scaler.capstone.product.services;

import com.scaler.capstone.product.dto.ProductDTO;
import com.scaler.capstone.product.exceptions.CategoryNotExistException;
import com.scaler.capstone.product.exceptions.InvalidDataException;
import com.scaler.capstone.product.exceptions.ProductNotExistException;
import com.scaler.capstone.product.models.product.Category;
import com.scaler.capstone.product.models.product.Product;
import com.scaler.capstone.product.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Product createProduct(ProductDTO productDTO) throws InvalidDataException, CategoryNotExistException {

        Optional<Category> existingCategory = categoryService.getCategoryByName(productDTO.getCategory());
        if(existingCategory.isEmpty()){
            throw new CategoryNotExistException("Category not exists : "+productDTO.getCategory());
        }
        Product product = new Product();
        product.setCategory(existingCategory.get());
        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setRating(productDTO.getRating());
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) throws ProductNotExistException {
        Optional<Product> product = productRepository.findById(id);

        if(product.isEmpty())
        {
            throw new ProductNotExistException("Product id not found: "+id);
        }

        if(product.get().isDeleted())
        {
            throw new ProductNotExistException("Product id: "+id+ " does not exist");
        }

        return product.get();
    }

    public Product updateProduct(Long id, ProductDTO productDTO) throws ProductNotExistException,CategoryNotExistException {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if(optionalProduct.isEmpty())
        {
            throw new ProductNotExistException("Product id not found: "+id);
        }

        Product product = optionalProduct.get();
        if(product.isDeleted())
        {
            throw new ProductNotExistException("Product id: "+id+ " does not exist");
        }

        if(productDTO.getTitle()!=null){
            product.setTitle(productDTO.getTitle());
        }

        if(productDTO.getDescription()!=null){
            product.setDescription(productDTO.getDescription());
        }

        if(productDTO.getPrice()!=null){
            product.setPrice(productDTO.getPrice());
        }

        if(productDTO.getRating()!=null){
            product.setRating(productDTO.getRating());
        }
        if(productDTO.getStockQuantity()!=null){
            product.setStockQuantity(product.getStockQuantity());
        }
        if(productDTO.getCategory()!=null){
            String existingCategoroy = productDTO.getCategory();
            if (!product.getCategory().getName().equalsIgnoreCase(existingCategoroy)) {
                Optional<Category> optionalCategory = categoryService.getCategoryByName(existingCategoroy);
                if (optionalCategory.isPresent()) {
                    product.setCategory(optionalCategory.get());
                } else {
                    throw new CategoryNotExistException("Category not exists : " + existingCategoroy);
                }
            }
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) throws ProductNotExistException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isEmpty())
        {
            throw new ProductNotExistException("Product id not found: "+id);
        }

        Product product = optionalProduct.get();
        if(product.isDeleted())
        {
            throw new ProductNotExistException("Product id: "+id+ " does not exist");
        }
        product.setDeleted(true);
    }

}
