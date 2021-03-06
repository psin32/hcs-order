package co.uk.app.commerce.order.application;

import static springfox.documentation.builders.PathSelectors.regex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableEurekaClient
@EnableSwagger2
@SpringBootApplication
@ComponentScan({ "co.uk.app.commerce.order", "co.uk.app.commerce.basket", "co.uk.app.commerce.address",
		"co.uk.app.commerce.summary", "co.uk.app.commerce.delivery", "co.uk.app.commerce.shipping",
		"co.uk.app.commerce.kafka", "co.uk.app.commerce.payment", "co.uk.app.commerce.additem" })
@EnableMongoRepositories(basePackages = { "co.uk.app.commerce.order", "co.uk.app.commerce.catalog",
		"co.uk.app.commerce.basket", "co.uk.app.commerce.address", "co.uk.app.commerce.shipping",
		"co.uk.app.commerce.summary" })
public class OrderApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
		LOGGER.info("Application started successfully");
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public Docket swaggerApiDocs() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("co.uk.app.commerce")).paths(regex("/*.*")).build();
	}

}
