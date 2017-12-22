package co.uk.app.commerce.shipping.service;

import java.util.List;

import co.uk.app.commerce.shipping.document.Shipping;

public interface ShippingService {

	Shipping save(Shipping shipping);
	
	Shipping getActiveShippingByName(String name);

	List<Shipping> getActiveShippingForCurrentOrder(Long usersId);
}
