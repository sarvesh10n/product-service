package com.scaler.capstone.project.services;

import com.scaler.capstone.project.dto.FakeStoreProductDTO;
import com.scaler.capstone.project.exceptions.ProductNotExistException;
import com.scaler.capstone.project.models.Category;
import com.scaler.capstone.project.models.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service("fakeStoreProductService")
@Log4j2
public class FakeStoreProductService implements ProductService {

    private RestTemplate restTemplate;

    @Autowired
    public FakeStoreProductService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    //ToDo : Need to implement all other 7 APIs of fake store    16 oct, 23 oct, 25 oct
    @Override
    public Product getSingleProduct(Long id) throws ProductNotExistException {

            FakeStoreProductDTO productDto = restTemplate.getForObject(
                    "https://fakestoreapi.com/products/" + id,
                    FakeStoreProductDTO.class);

            if (productDto == null) {
                throw new ProductNotExistException(
                        "Product with id: " + id + " doesn't exist."
                );
            }

            return convertFakeStoreToProduct(productDto);

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

    @Override
    public Product updateProduct(Long id, Product product) {
        return null;
    }

    @Override
    public Product replaceProduct(Long id, Product product) {
        FakeStoreProductDTO fakeStoreProductDto = new FakeStoreProductDTO();
        fakeStoreProductDto.setTitle(product.getTitle());
        fakeStoreProductDto.setPrice(product.getPrice());
        fakeStoreProductDto.setImage(product.getDescription());
        fakeStoreProductDto.setImage(product.getImageUrl());

        RequestCallback requestCallback = restTemplate.httpEntityCallback(fakeStoreProductDto, FakeStoreProductDTO.class);
        HttpMessageConverterExtractor<FakeStoreProductDTO> responseExtractor =
                new HttpMessageConverterExtractor<>(FakeStoreProductDTO.class, restTemplate.getMessageConverters());
        FakeStoreProductDTO response = restTemplate.execute("https://fakestoreapi.com/products/" + id, HttpMethod.PUT, requestCallback, responseExtractor);

        return convertFakeStoreToProduct(response);
    }

    @Override
    public Product addNewProduct(Product product) {
        return null;
    }

    @Override
    public boolean deleteProduct(Long id) {
        return false;
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
