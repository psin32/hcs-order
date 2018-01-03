package co.uk.app.commerce.shipping.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.repository.OrdersRepository;
import co.uk.app.commerce.order.service.OrdersService;
import co.uk.app.commerce.shipping.document.Shipping;
import co.uk.app.commerce.shipping.repository.ShippingRepository;

@Component
public class ShippingServiceImpl implements ShippingService {

	@Autowired
	private ShippingRepository shippingRepository;

	@Autowired
	private OrdersService ordersService;

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
	public List<Shipping> getActiveShippingForCurrentOrder(String usersId) {
		String type = null;
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);
		if (null != orders) {
			Address shippingAddress = orders.getShippingaddress();
			if (null != shippingAddress) {
				String country = shippingAddress.getCountry();
				if (null != country && country.equalsIgnoreCase("United Kingdom")) {
					type = OrderConstants.SHIPPING_TYPE_UK;
				} else {
					type = OrderConstants.SHIPPING_TYPE_NONUK;
				}
			}
		}
		return shippingRepository.findByTypeAndActiveIsTrue(type);
	}

}
