package com.scaler.capstone.product.models;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User {
    @Id
    private Long id;
    private String name;
    private String email;
    private String address;
    @ElementCollection
    private List<String> roles;
}
