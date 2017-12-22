package co.uk.app.commerce.order.service;

import java.util.List;

import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.document.Orders;

public interface OrdersService {

	Orders getOrderSummary(Long usersId);

	List<Address> getActiveShippingAddresses(Long usersId);

	Orders saveDeliveryOption(Long usersId, OrderType orderType);
}
