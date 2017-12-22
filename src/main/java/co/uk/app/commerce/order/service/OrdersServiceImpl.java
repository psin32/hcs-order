package co.uk.app.commerce.order.service;

import java.util.List;
import java.util.stream.Collectors;

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
	public List<Address> getActiveShippingAddresses(Long usersId) {
		return addressRepository
				.findByUsersIdAndStatusAndSelfaddress(usersId, OrderConstants.ADDRESS_ACTIVE_STATUS,
						OrderConstants.NON_SELFADDRESS)
				.stream().filter(address -> address.getAddresstype().contains(OrderConstants.ADDRESS_SHIPPING_TYPE))
				.collect(Collectors.toList());
	}

	@Override
	public Orders saveDeliveryOption(Long usersId, OrderType orderType) {
		Orders orders = ordersRepository.findByUsersIdAndStatus(usersId, OrderConstants.ORDER_STATUS_PENDING);
		if(null != orders) {
			orders.setOrdertype(orderType);
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

}
