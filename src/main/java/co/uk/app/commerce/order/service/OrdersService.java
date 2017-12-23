package co.uk.app.commerce.order.service;

import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.document.Orders;

public interface OrdersService {

	Orders getOrderSummary(Long usersId);

	Orders saveDeliveryOption(Long usersId, OrderType orderType);
	
	Orders saveDeliveryAddress(Long usersId, Address address);
}
