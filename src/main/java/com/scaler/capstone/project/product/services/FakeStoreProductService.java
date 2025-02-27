package com.scaler.capstone.project.product.services;

import com.scaler.capstone.project.product.dto.FakeStoreProductDTO;
import com.scaler.capstone.project.product.models.Category;
import com.scaler.capstone.project.product.models.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class FakeStoreProductService implements ProductService {

    private RestTemplate restTemplate;

    @Autowired
    public FakeStoreProductService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    //ToDo : Need to implement all other 7 APIs of fake store    16 oct, 23 oct, 25 oct
    @Override
    public Product getSingleProduct(Long id) {
        try {
            FakeStoreProductDTO response = restTemplate.getForObject(
                    "https://fakestoreapi.com/products/" + id,
                    FakeStoreProductDTO.class);

            return convertFakeStoreToProduct(response);
        }catch (Exception e){
            log.error("Some Exception occurred",e);
            return null;
        }
    }

    @Override
    public FakeStoreProductDTO addNewProduct(FakeStoreProductDTO product) {
        try {
            FakeStoreProductDTO response = restTemplate.postForObject("https://fakestoreapi.com/products", product, FakeStoreProductDTO.class);
            return response;
        }catch (Exception e){
            log.error("Some Exception occurred",e);
            return null;
        }
    }

    @Override
    public List<Product> getAllProducts() {
        FakeStoreProductDTO[] response = restTemplate.getForObject(
                "https://fakestoreapi.com/products",
                FakeStoreProductDTO[].class
        );


        List<Product> answer = new ArrayList<>();


        for (FakeStoreProductDTO dto: response) {
            answer.add(convertFakeStoreToProduct(dto));
        }

        return answer;
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
