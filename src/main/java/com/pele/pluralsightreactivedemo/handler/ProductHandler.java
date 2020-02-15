package com.pele.pluralsightreactivedemo.handler;

import com.pele.pluralsightreactivedemo.model.Product;
import com.pele.pluralsightreactivedemo.model.ProductEvent;
import com.pele.pluralsightreactivedemo.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
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

	public Mono<ServerResponse> saveProduct(ServerRequest request) {
		final Mono<Product> productMono = request.bodyToMono(Product.class);

		return productMono
				.flatMap(product ->
						ServerResponse
								.status(HttpStatus.CREATED)
								.contentType(APPLICATION_JSON)
								.body(repository.save(product), Product.class));
	}

	public Mono<ServerResponse> updateProduct(ServerRequest request) {
		String id = request.pathVariable("id");

		Mono<Product> existingProductMono = this.repository.findById(id);
		Mono<Product> requestProductMono = request.bodyToMono(Product.class);

		return requestProductMono
				.zipWith(existingProductMono, (product, existingProduct) ->
						new Product(existingProduct.getId(), product.getName(), product.getPrice()))
				.flatMap(product ->
						ServerResponse.ok()
								.contentType(APPLICATION_JSON)
								.body(repository.save(product), Product.class))
				.switchIfEmpty(NOT_FOUND_RESPONSE);
	}

	public Mono<ServerResponse> deleteProduct(ServerRequest request) {
		String id = request.pathVariable("id");

		Mono<Product> productMono = this.repository.findById(id);

		return productMono
				.flatMap(product ->
						ServerResponse.ok()
								.build(repository.delete(product))
				)
				.switchIfEmpty(NOT_FOUND_RESPONSE);
	}

	public Mono<ServerResponse> deleteAllProducts(ServerRequest request) {
		return ServerResponse.ok().build(repository.deleteAll());
	}

	public Mono<ServerResponse> getProductEvents(ServerRequest request) {
		Flux<ProductEvent> eventsFlux = Flux
				.interval(Duration.ofSeconds(1))
				.map(val -> new ProductEvent(val, "Product Event" + val));

		return ServerResponse.ok()
				.contentType(TEXT_EVENT_STREAM)
				.body(eventsFlux, ProductEvent.class);
	}


}