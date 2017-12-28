package co.uk.app.commerce.order.service;

import co.uk.app.commerce.additem.bean.AddItemBean;
import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.order.bean.OrderConfirmationBean;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.document.Orders;

public interface OrdersService {
	
	Orders save(Orders orders);

	Orders addItem(AddItemBean addItemBean, Long usersId, String currency);

	Orders updateBasket(AddItemBean addItemBean, Long usersId, String currency);

	Orders deleteItem(String partnumber, Long usersId, String currency);

	Orders getPendingOrderByUsersId(Long usersId);

	Orders saveDeliveryOption(Long usersId, OrderType orderType);

	Orders saveDeliveryAddress(Long usersId, Address address);

	Orders saveShippingMethod(Long usersId, String shippingName);

	Orders confirmOrder(Long usersId, OrderConfirmationBean orderConfirmationBean);

}
