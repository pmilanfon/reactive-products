package com.pele.pluralsightreactivedemo;

import com.pele.pluralsightreactivedemo.model.Product;
import com.pele.pluralsightreactivedemo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Flux;

@Slf4j
@SpringBootApplication
public class PluralsightReactiveDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PluralsightReactiveDemoApplication.class, args);
    }

    @Bean
    @Order(1)
    CommandLineRunner initCmdRunner(ProductRepository productRepository) {
        return args -> {
            log.info("========================================= Starting from CommandLineRunner =========================================");
            /*Flux<Product> productFlux = */
            Flux.just(
                    createNewProduct(null, "Big Latte", 2.99, "CmdLineRunner"),
                    createNewProduct(null, "Small Espresso", 2.3, "CmdLineRunner"),
                    createNewProduct(null, "Green Tea", 1.99, "CmdLineRunner"))
                    .flatMap(productRepository::save);

            log.info("Saved all products");
        };
    }

    @Bean
    @Order(2)
    ApplicationRunner initAppRunner(ProductRepository productRepository) {
        return args -> {
            log.info("========================================= Starting from ApplicationRunner =========================================");
			/*Flux<Product> productFlux =*/
            Flux.just(
                    createNewProduct(null, "Big Lattez", 2.99, "ApplicationRunner"),
                    createNewProduct(null, "Small Espressos", 2.3, "ApplicationRunner"),
                    createNewProduct(null, "Green Teas", 1.99, "ApplicationRunner"))
                    .flatMap(productRepository::save)
                    .subscribe();
            //.subscribe();

            log.info("Saved all products");
        };

    }

    private Product createNewProduct(String id, String name, Double price, String whoRan) {
        log.info("Creating new product in thread [{}], started by: {}", Thread.currentThread().getName(), whoRan);

        return new Product(id, name, price);
    }


}
