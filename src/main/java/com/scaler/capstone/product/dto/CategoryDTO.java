package com.scaler.capstone.product.dto;

import com.scaler.capstone.product.models.product.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryDTO {
    private Long id;
    private String name;


    public static CategoryDTO fromCategory(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(categoryDTO.getName());
        return categoryDTO;
    }
}
