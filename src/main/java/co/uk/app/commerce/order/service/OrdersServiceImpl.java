package co.uk.app.commerce.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.address.repository.AddressRepository;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.repository.OrdersRepository;

@Component
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private OrdersRepository ordersRepository;

	@Override
	public Orders saveDeliveryOption(Long usersId, OrderType orderType) {
		Orders orders = ordersRepository.findByUsersIdAndStatus(usersId, OrderConstants.ORDER_STATUS_PENDING);
		if (null != orders) {
			orders.setOrdertype(orderType);
			orders.setShippingaddress(null);
			orders = ordersRepository.save(orders);
		}
		return orders;
	}

	@Override
	public Orders getOrderSummary(Long usersId) {
		Orders orders = null;
		if (null != usersId) {
			orders = ordersRepository.findByUsersIdAndStatus(usersId, OrderConstants.ORDER_STATUS_PENDING);
		}
		return orders;
	}

	@Override
	public Orders saveDeliveryAddress(Long usersId, Address address) {
		Address shippingAddress = addressRepository.findByUsersIdAndAddressId(usersId, address.getAddressId());
		if (null == shippingAddress) {
			shippingAddress = address;
		}
		Orders orders = ordersRepository.findByUsersIdAndStatus(usersId, OrderConstants.ORDER_STATUS_PENDING);
		orders.setShippingaddress(shippingAddress);
		return ordersRepository.save(orders);
	}
}
