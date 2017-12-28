package co.uk.app.commerce.order.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paypal.api.payments.Payment;

import co.uk.app.commerce.additem.bean.AddItemBean;
import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.address.repository.AddressRepository;
import co.uk.app.commerce.basket.bean.Items;
import co.uk.app.commerce.catalog.bean.Image;
import co.uk.app.commerce.catalog.bean.ListPrice;
import co.uk.app.commerce.catalog.document.Catentry;
import co.uk.app.commerce.catalog.repository.CatentryRepository;
import co.uk.app.commerce.order.bean.OrderConfirmationBean;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.bean.PayeeBean;
import co.uk.app.commerce.order.bean.PayerBean;
import co.uk.app.commerce.order.bean.PaymentBean;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.repository.OrdersRepository;
import co.uk.app.commerce.payment.service.PaymentService;
import co.uk.app.commerce.shipping.document.Shipping;
import co.uk.app.commerce.shipping.repository.ShippingRepository;

@Component
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private ShippingRepository shippingRepository;

	@Autowired
	private CatentryRepository catentryRepository;

	@Autowired
	private PaymentService paymentService;

	@Override
	public Orders saveDeliveryOption(Long usersId, OrderType orderType) {
		Orders orders = ordersRepository.findByUsersIdAndStatus(usersId, OrderConstants.ORDER_STATUS_PENDING);
		if (null != orders) {
			orders.setOrdertype(orderType);
			orders.setShippingaddress(null);
			orders.setShippingmethod(null);
			orders.setShippingcharges(null);
			orders.setOrdertotal(orders.getSubtotal());
			orders = ordersRepository.save(orders);
		}
		return orders;
	}

	@Override
	public Orders getPendingOrderByUsersId(Long usersId) {
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
		orders.setShippingmethod(null);
		orders.setShippingcharges(null);
		orders.setOrdertotal(orders.getSubtotal());
		return ordersRepository.save(orders);
	}

	@Override
	public Orders saveShippingMethod(Long usersId, String shippingName) {
		Shipping shipping = shippingRepository.findByName(shippingName);
		Orders updatedOrder = null;
		if (null != shipping) {
			Orders orders = ordersRepository.findByUsersIdAndStatus(usersId, OrderConstants.ORDER_STATUS_PENDING);
			if (null != orders) {
				orders.setShippingmethod(shipping.getName());
				orders.setShippingcharges(shipping.getAmount());
				orders.setOrdertotal(orders.getSubtotal() + orders.getShippingcharges());
				updatedOrder = ordersRepository.save(orders);
			}
		}
		return updatedOrder;
	}

	@Override
	public Orders confirmOrder(Long usersId, OrderConfirmationBean orderConfirmationBean) {
		Orders orders = ordersRepository.findByUsersIdAndStatus(usersId, OrderConstants.ORDER_STATUS_PENDING);

		if (null != orders) {
			Payment paymentAfterGet = paymentService.getPaypalPaymentDetails(usersId,
					orders.getPaypalPayment().getPaymentId());

			Payment paymentAfterExecute = paymentService.executePayment(paymentAfterGet, orders);

			PaymentBean paymentBean = orders.getPaypalPayment();
			paymentBean.setPaymentId(paymentAfterExecute.getId());
			paymentBean.setStatus(paymentAfterExecute.getState());
			paymentBean.setIntent(paymentAfterExecute.getIntent());
			paymentBean.setCreateTime(paymentAfterExecute.getCreateTime());
			paymentBean.setUpdateTime(paymentAfterExecute.getUpdateTime());
			paymentBean.setCartId(paymentAfterExecute.getCart());

			if (null != paymentAfterExecute && null != paymentAfterExecute.getPayer()
					&& null != paymentAfterExecute.getPayer().getPayerInfo()) {
				PayerBean payerBean = new PayerBean();
				payerBean.setEmail(paymentAfterExecute.getPayer().getPayerInfo().getEmail());
				payerBean.setFirstname(paymentAfterExecute.getPayer().getPayerInfo().getFirstName());
				payerBean.setLastname(paymentAfterExecute.getPayer().getPayerInfo().getLastName());
				payerBean.setPayerId(paymentAfterExecute.getPayer().getPayerInfo().getPayerId());
				payerBean.setStatus(paymentAfterExecute.getPayer().getStatus());

				paymentBean.setPayer(payerBean);
			}

			if (null != paymentAfterExecute && null != paymentAfterExecute.getPayee()) {
				PayeeBean payeeBean = new PayeeBean();
				payeeBean.setEmail(paymentAfterExecute.getPayee().getEmail());
				payeeBean.setMerchantId(paymentAfterExecute.getPayee().getMerchantId());

				paymentBean.setPayee(payeeBean);
			}

			orders.setPaypalPayment(paymentBean);

			if (null != paymentAfterExecute && null != paymentAfterExecute.getState()
					&& paymentAfterExecute.getState().equalsIgnoreCase(OrderConstants.PAYMENT_STATUS_APPROVED)) {
				orders.setStatus(OrderConstants.ORDER_STATUS_COMPLETE);
			}
			orders = ordersRepository.save(orders);
		} else {
			Orders completedOrder = ordersRepository.findByUsersIdAndPaypalPaymentPaymentId(usersId,
					orderConfirmationBean.getPaymentId());
			if (null != completedOrder
					&& completedOrder.getStatus().equalsIgnoreCase(OrderConstants.ORDER_STATUS_COMPLETE)) {
				return completedOrder;
			}
		}
		return orders;
	}

	@Override
	public Orders addItem(AddItemBean addItemBean, Long usersId, String currency) {
		Catentry catentry = catentryRepository.findByPartnumber(addItemBean.getPartnumber());
		Orders orders = null;
		if (null != catentry && null != usersId) {
			orders = getPendingOrderByUsersId(usersId);
			List<Items> items = new ArrayList<>();
			if (null == orders) {
				orders = new Orders();
				orders.setUsersId(usersId);
				orders.setOrdersId(UUID.randomUUID().toString());
				orders.setStatus("P");
			} else {
				items = orders.getItems();
			}
			items = getItemDetails(addItemBean, currency, catentry, items, true);
			orders.setItems(items);
			DecimalFormat formatter = new DecimalFormat("#0.00");
			orders.setSubtotal(
					Double.valueOf(formatter.format(items.stream().mapToDouble(item -> item.getItemtotal()).sum())));
			orders.setShippingaddress(null);
			orders.setShippingcharges(null);
			orders.setOrdertype(null);
			orders.setShippingmethod(null);
			orders.setOrdertotal(
					Double.valueOf(formatter.format(items.stream().mapToDouble(item -> item.getItemtotal()).sum())));
		}
		Orders updatedOrders = new Orders();
		if (null != orders) {
			if (orders.getItems().isEmpty()) {
				ordersRepository.delete(orders.getId());
			} else {
				updatedOrders = ordersRepository.save(orders);
			}
		}
		return updatedOrders;
	}

	private Double calculateItemTotal(Double listPrice, Integer quantity) {
		Double itemTotal = listPrice * quantity;
		DecimalFormat formatter = new DecimalFormat("#0.00");
		return Double.valueOf(formatter.format(itemTotal));
	}

	private List<Items> getItemDetails(AddItemBean addItemBean, String currency, Catentry catentry,
			List<Items> itemsList, boolean isItemAdded) {
		String partnumber = addItemBean.getPartnumber();
		Integer quantity = addItemBean.getQuantity();
		List<Items> newList = new ArrayList<>();
		Items items = null;

		if (null != itemsList && itemsList.size() > 0) {
			items = itemsList.stream().filter(item -> item.getPartnumber().equalsIgnoreCase(partnumber)).findAny()
					.orElse(null);
			newList = itemsList.stream().filter(item -> !item.getPartnumber().equalsIgnoreCase(partnumber))
					.collect(Collectors.toList());
		}

		if (null == items) {
			items = new Items();
			items.setQuantity(quantity);
		} else {
			items.setQuantity(quantity + items.getQuantity());
		}

		if (!isItemAdded) {
			items.setQuantity(quantity);
		}

		items.setPartnumber(partnumber);
		items.setName(catentry.getDescription().getName());
		items.setCurrency(currency);
		items.setUrl(catentry.getUrl());

		List<ListPrice> listPrices = catentry.getListprice();
		ListPrice price = listPrices.stream().filter(prices -> prices.getCurrency().equalsIgnoreCase(currency))
				.findAny().orElse(null);
		if (null != price) {
			Double listPrice = Double.valueOf(price.getPrice());
			items.setListprice(listPrice);

			items.setItemtotal(calculateItemTotal(listPrice, items.getQuantity()));
		} else {
			// TODO: Throw exception here in case price does not exists
			// for this product.
		}

		List<Image> thumbnailImages = catentry.getThumbnail();

		Image image = thumbnailImages.stream().filter(images -> images.getName().equalsIgnoreCase("front-view"))
				.findAny().orElse(null);
		if (null != image) {
			items.setImage(image.getUrl());
		} else {
			// TODO: Add default image if image does not exists.
		}
		if (items.getQuantity() > 0) {
			newList.add(items);
		}
		Collections.sort(newList, (p1, p2) -> p1.getPartnumber().compareTo(p2.getPartnumber()));
		return newList;
	}

	@Override
	public Orders updateBasket(AddItemBean addItemBean, Long usersId, String currency) {
		Catentry catentry = catentryRepository.findByPartnumber(addItemBean.getPartnumber());
		Orders orders = null;
		if (null != catentry) {
			orders = getPendingOrderByUsersId(usersId);
			List<Items> items = new ArrayList<>();
			if (null == orders) {
				orders = new Orders();
				orders.setUsersId(usersId);
				orders.setStatus("P");
			} else {
				items = orders.getItems();
			}
			items = getItemDetails(addItemBean, currency, catentry, items, false);
			orders.setItems(items);

			DecimalFormat formatter = new DecimalFormat("#0.00");
			orders.setSubtotal(
					Double.valueOf(formatter.format(items.stream().mapToDouble(item -> item.getItemtotal()).sum())));
			orders.setShippingaddress(null);
			orders.setShippingcharges(null);
			orders.setOrdertype(null);
			orders.setShippingmethod(null);
			orders.setOrdertotal(
					Double.valueOf(formatter.format(items.stream().mapToDouble(item -> item.getItemtotal()).sum())));
		}
		Orders updatedOrders = new Orders();
		if (null != orders) {
			if (orders.getItems().isEmpty()) {
				ordersRepository.delete(orders.getId());
			} else {
				updatedOrders = ordersRepository.save(orders);
			}
		}
		return updatedOrders;
	}

	@Override
	public Orders deleteItem(String partnumber, Long usersId, String currency) {
		Orders orders = getPendingOrderByUsersId(usersId);
		List<Items> items = null;
		Orders updatedOrders = new Orders();
		if (null != orders) {
			items = orders.getItems();
			if (null != items && !items.isEmpty()) {
				orders.setItems(items.stream().filter(item -> !item.getPartnumber().equalsIgnoreCase(partnumber))
						.collect(Collectors.toList()));
				if (orders.getItems().isEmpty()) {
					ordersRepository.delete(orders.getId());
				} else {
					DecimalFormat formatter = new DecimalFormat("#0.00");
					orders.setSubtotal(Double.valueOf(formatter
							.format(orders.getItems().stream().mapToDouble(item -> item.getItemtotal()).sum())));
					orders.setShippingaddress(null);
					orders.setShippingcharges(null);
					orders.setOrdertype(null);
					orders.setShippingmethod(null);
					orders.setOrdertotal(Double.valueOf(formatter
							.format(orders.getItems().stream().mapToDouble(item -> item.getItemtotal()).sum())));
					updatedOrders = ordersRepository.save(orders);
				}
			}
		}
		return updatedOrders;
	}

	@Override
	public Orders save(Orders orders) {
		return ordersRepository.save(orders);
	}
}
