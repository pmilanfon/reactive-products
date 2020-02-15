package com.pele.pluralsightreactivedemo;

import com.pele.pluralsightreactivedemo.handler.ProductHandler;
import com.pele.pluralsightreactivedemo.model.Product;
import com.pele.pluralsightreactivedemo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

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
			final Flux<Product> productFlux = Flux.just(
					createNewProduct(null, "Big Latte", 2.99, "CmdLineRunner"),
					createNewProduct(null, "Small Espresso", 2.3, "CmdLineRunner"),
					createNewProduct(null, "Green Tea", 1.99, "CmdLineRunner"))
					.flatMap(productRepository::save);

			productFlux
					.thenMany(productRepository.findAll())
					.subscribe(System.out::println);

			log.info("Saved all products");

		};
	}

	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler handler) {
        /*return RouterFunctions
                .route(RequestPredicates.GET("/products").and(RequestPredicates.accept(APPLICATION_JSON)), handler::getAllProducts)
                .andRoute(RequestPredicates.POST("/products").and(RequestPredicates.contentType(APPLICATION_JSON)), handler::saveProduct)
                .andRoute(RequestPredicates.DELETE("/products").and(RequestPredicates.contentType(APPLICATION_JSON)), handler::deleteAllProducts)
                .andRoute(RequestPredicates.DELETE("/products/events").and(RequestPredicates.contentType(TEXT_EVENT_STREAM)), handler::getProductEvents)
                .andRoute(RequestPredicates.DELETE("/products/{id}").and(RequestPredicates.contentType(APPLICATION_JSON)), handler::getProduct)
                .andRoute(RequestPredicates.PUT("/products/{id}").and(RequestPredicates.contentType(APPLICATION_JSON)), handler::updateProduct)
                .andRoute(RequestPredicates.DELETE("/products/{id}").and(RequestPredicates.contentType(APPLICATION_JSON)), handler::deleteProduct);*/
		return
				nest(path("/products"),
						nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
								route(GET("/"), handler::getAllProducts)
										.andRoute(method(HttpMethod.POST), handler::saveProduct)
										.andRoute(DELETE("/"), handler::deleteAllProducts)
										.andRoute(GET("/events"), handler::getProductEvents))
										.andNest(path("/{id}"),
												route(method(HttpMethod.GET), handler::getProduct)
												.andRoute(method(HttpMethod.PUT), handler::updateProduct)
												.andRoute(method(HttpMethod.DELETE), handler::deleteProduct)
						)
				);
	}

    /*@Bean
    @Order(2)
    ApplicationRunner initAppRunner(ProductRepository productRepository) {
        return args -> {
            log.info("========================================= Starting from ApplicationRunner =========================================");
			*//*Flux<Product> productFlux =*//*
            Flux.just(
                    createNewProduct(null, "Big Lattez", 2.99, "ApplicationRunner"),
                    createNewProduct(null, "Small Espressos", 2.3, "ApplicationRunner"),
                    createNewProduct(null, "Green Teas", 1.99, "ApplicationRunner"))
                    .flatMap(productRepository::save)
                    .subscribe();
            //.subscribe();

            log.info("Saved all products");
        };

    }*/

	private Product createNewProduct(String id, String name, Double price, String whoRan) {
		log.info("Creating new product in thread [{}], started by: {}", Thread.currentThread().getName(), whoRan);

		return new Product(id, name, price);
	}


}
