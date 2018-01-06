package co.uk.app.commerce.order.service;

import java.util.Collection;

import co.uk.app.commerce.additem.bean.AddItemBean;
import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.order.bean.OrderConfirmationBean;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.exception.OrdersApplicationException;

public interface OrdersService {

	Orders save(Orders orders);

	Orders addItem(AddItemBean addItemBean, String usersId, String currency);

	Orders updateBasket(AddItemBean addItemBean, String usersId, String currency);

	Orders deleteItem(String partnumber, String usersId, String currency);

	Orders getPendingOrderByUsersId(String usersId);
	
	Orders getPlacedOrderByUsersIdAndOrdersId(String usersId, String ordersId);
	
	Collection<Orders> getPlacedOrdersByUsersId(String usersId);

	Orders saveDeliveryOption(String usersId, OrderType orderType);

	Orders saveDeliveryAddress(String usersId, Address address);

	Orders saveShippingMethod(String usersId, String shippingName);

	Orders confirmOrder(String usersId, OrderConfirmationBean orderConfirmationBean) throws OrdersApplicationException;

	String generateToken(String usersId);
	
	Orders mergeOrders(String usersId, String guestUserId);
}
