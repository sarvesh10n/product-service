package com.scaler.capstone.project.product.services;

import com.scaler.capstone.project.product.dto.FakeStoreProductDTO;
import com.scaler.capstone.project.product.models.Category;
import com.scaler.capstone.project.product.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FakeStoreProductService implements ProductService{

    private RestTemplate restTemplate;

    @Autowired
    public FakeStoreProductService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    //ToDo : Need to implement all other 7 APIs of fake store    16 oct, 23 oct, 25 oct
    @Override
    public Product getSingleProduct(Long id) {
        FakeStoreProductDTO response = restTemplate.getForObject(
                "https://fakestoreapi.com/products/"+id,
                FakeStoreProductDTO.class);

        return convertFakeStoreToProduct(response);
    }

    @Override
    public FakeStoreProductDTO addNewProduct(FakeStoreProductDTO product) {
        FakeStoreProductDTO response = restTemplate.postForObject("https://fakestoreapi.com/products",product,FakeStoreProductDTO.class);
        return response;
    }

    @Override
    public List<Product> getAllProducts() {
        //Todo Need add Product [].Class instead List.Class
        List<Product> productList = restTemplate.getForObject("https://fakestoreapi.com/products", List.class);
        return productList;
    }

    private Product convertFakeStoreToProduct(FakeStoreProductDTO FakeStoreProductDTO ) {
        Product product = new Product();
        product.setId(FakeStoreProductDTO.getId());
        product.setTitle(FakeStoreProductDTO.getTitle());
        product.setPrice(FakeStoreProductDTO.getPrice());
        product.setDescription(FakeStoreProductDTO.getDescription());
        product.setCategory(new Category());
        product.getCategory().setName(FakeStoreProductDTO.getCategory());
        product.setImageUrl(FakeStoreProductDTO.getImage());
        return product;
    }
}
