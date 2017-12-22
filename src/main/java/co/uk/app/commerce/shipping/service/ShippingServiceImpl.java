package co.uk.app.commerce.shipping.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.uk.app.commerce.shipping.document.Shipping;
import co.uk.app.commerce.shipping.repository.ShippingRepository;

@Component
public class ShippingServiceImpl implements ShippingService {

	@Autowired
	private ShippingRepository shippingRepository;

	@Override
	public Shipping save(Shipping shipping) {
		Shipping updatedShipping = null;
		if (null != shipping && null != shipping.getName()) {
			Shipping existingShipping = shippingRepository.findByName(shipping.getName());
			if (null != existingShipping) {
				shipping.setId(existingShipping.getId());
			}
			updatedShipping = shippingRepository.save(shipping);
		}
		return updatedShipping;
	}

	@Override
	public Shipping getActiveShippingByName(String name) {
		return shippingRepository.findByNameAndActiveIsTrue(name);
	}

	@Override
	public List<Shipping> getActiveShippingForCurrentOrder(Long usersId) {
		String type = null;
		return shippingRepository.findByTypeAndActiveIsTrue(type);
	}

}
