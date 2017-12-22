package co.uk.app.commerce.shipping.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.shipping.document.Shipping;

public interface ShippingRepository extends MongoRepository<Shipping, String> {

	Shipping findByName(String name);
	
	Shipping findByNameAndActiveIsTrue(String name);

	List<Shipping> findByTypeAndActiveIsTrue(String type);
}
