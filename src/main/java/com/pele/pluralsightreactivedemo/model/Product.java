package com.pele.pluralsightreactivedemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    private String nanem;

    private Double price;

}
