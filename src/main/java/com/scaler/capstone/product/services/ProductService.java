package com.scaler.capstone.product.services;

import com.scaler.capstone.product.dto.CreateProductDTO;
import com.scaler.capstone.product.exceptions.InvalidDataException;
import com.scaler.capstone.product.exceptions.ProductNotExistException;
import com.scaler.capstone.product.models.product.Category;
import com.scaler.capstone.product.models.product.Product;
import com.scaler.capstone.product.repositories.CategoryRepository;
import com.scaler.capstone.product.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(CreateProductDTO createProductDTO) throws InvalidDataException {


        Optional<Category> existingCategory = categoryRepository.findByName(createProductDTO.getCategory());
        Category addCategory;
        if (existingCategory.isPresent()) {
            addCategory = existingCategory.get();
        }
        else {
            addCategory = new Category();
            addCategory.setName(createProductDTO.getCategory());
            categoryRepository.save(addCategory);
        }
        Product product = new Product();
        product.setCategory(addCategory);
        product.setTitle(createProductDTO.getTitle());
        product.setDescription(createProductDTO.getDescription());
        product.setPrice(createProductDTO.getPrice());
        product.setStockQuantity(createProductDTO.getStockQuantity());
        product.setRating(createProductDTO.getRating());
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

    public Product updateProduct(Long id, Map<String, Object> updates) throws ProductNotExistException {
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

        updates.forEach((key, value) -> {
            switch (key) {
                case "title":
                    product.setTitle((String) value);
                    break;
                case "description":
                    product.setDescription((String) value);
                    break;
                case "price":
                    product.setPrice((Double) value);
                    break;
                case "rating":
                    product.setRating((Double) value);
                    break;
                case "stockQuantity":
                    product.setRating((Integer) value);
                    break;
                case "category":
                    String mapCategory = ((String) value);
                    if(!product.getCategory().getName().equalsIgnoreCase(mapCategory))
                    {
                        Optional<Category> optionalCategory = categoryRepository.findByName(mapCategory);
                        if(optionalCategory.isPresent()){
                            product.setCategory(optionalCategory.get());
                        }
                        else {
                            Category addCategory = new Category();
                            addCategory.setName(mapCategory);
                            categoryRepository.save(addCategory);
                            product.setCategory(addCategory);
                        }
                    }
                    break;
            }
        });
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
