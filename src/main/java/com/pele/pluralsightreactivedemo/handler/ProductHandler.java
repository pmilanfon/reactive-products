package com.pele.pluralsightreactivedemo.handler;

import com.pele.pluralsightreactivedemo.model.Product;
import com.pele.pluralsightreactivedemo.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ProductHandler {

	private ProductRepository repository;

	private static final Mono<ServerResponse> NOT_FOUND_RESPONSE = ServerResponse.notFound().build();

	public ProductHandler(ProductRepository repository) {
		this.repository = repository;
	}

	public Mono<ServerResponse> getAllProducts(ServerRequest request) {
		Flux<Product> products = repository.findAll();

		return ServerResponse.ok()
				.contentType(APPLICATION_JSON)
				.body(products, Product.class);
	}

	public Mono<ServerResponse> getProduct(ServerRequest request) {
		String id = request.pathVariable("id");

		Mono<Product> productMono = this.repository.findById(id);

		return productMono
				.flatMap(product ->
						ServerResponse.ok()
						.contentType(APPLICATION_JSON)
						.body(fromValue(product)))
				.switchIfEmpty(NOT_FOUND_RESPONSE);
	}


}