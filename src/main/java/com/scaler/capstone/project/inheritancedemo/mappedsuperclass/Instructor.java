package com.scaler.capstone.project.inheritancedemo.mappedsuperclass;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "ms_instructor")
public class Instructor extends User {
//    @Id
//    private Long id;
    private String favouriteStudent;
}
