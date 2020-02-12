package com.pele.pluralsightreactivedemo.repository;

import com.pele.pluralsightreactivedemo.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
