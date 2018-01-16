package co.uk.app.commerce.order.service;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import co.uk.app.commerce.additem.bean.AddItemBean;
import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.basket.bean.Items;
import co.uk.app.commerce.catalog.bean.Image;
import co.uk.app.commerce.catalog.bean.ListPrice;
import co.uk.app.commerce.catalog.document.Catentry;
import co.uk.app.commerce.catalog.repository.CatentryRepository;
import co.uk.app.commerce.order.bean.GlobalCollectResponse;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.bean.PaypalPayerBean;
import co.uk.app.commerce.order.bean.PaypalPaymentBean;
import co.uk.app.commerce.order.bean.PaypalResponseBean;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.repository.OrdersRepository;
import co.uk.app.commerce.order.security.OrdersSecurityConfiguration;
import co.uk.app.commerce.order.util.PriceFormattingUtil;
import co.uk.app.commerce.shipping.document.Shipping;
import co.uk.app.commerce.shipping.repository.ShippingRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private ShippingRepository shippingRepository;

	@Autowired
	private CatentryRepository catentryRepository;

	@Autowired
	private OrdersSecurityConfiguration securityConfiguration;

	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

	@Override
	public Orders saveDeliveryOption(String usersId, OrderType orderType) {
		Orders orders = getPendingOrderByUsersId(usersId);
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
	public Orders getPendingOrderByUsersId(String usersId) {
		Orders order = null;
		if (null != usersId) {
			Sort sort = new Sort(Direction.ASC, "updateddate");
			Collection<Orders> orders = ordersRepository.findByUsersIdAndStatus(usersId,
					OrderConstants.ORDER_STATUS_PENDING, sort);
			if (null != orders) {
				order = orders.stream()
						.filter(ord -> ord.getStatus().equalsIgnoreCase(OrderConstants.ORDER_STATUS_PENDING))
						.findFirst().orElse(null);
			}
		}
		return order;
	}

	@Override
	public Orders getPlacedOrderByUsersIdAndOrdersId(String usersId, String ordersId) {
		Orders orders = null;
		if (null != usersId) {
			orders = ordersRepository.findByUsersIdAndStatusAndOrdersId(usersId, OrderConstants.ORDER_STATUS_COMPLETE,
					ordersId);
		}
		return orders;
	}

	@Override
	public Collection<Orders> getPlacedOrdersByUsersId(String usersId) {
		Collection<Orders> orders = null;
		if (null != usersId) {
			Sort sort = new Sort(Direction.DESC, "updateddate");
			orders = ordersRepository.findByUsersIdAndStatus(usersId, OrderConstants.ORDER_STATUS_COMPLETE, sort);
		}
		return orders;
	}

	@Override
	public Orders saveDeliveryAddress(String usersId, Address address) {
		if (null != address && address.getAddressId() == null) {
			address.setAddressId(0L);
		}
		Orders orders = getPendingOrderByUsersId(usersId);
		orders.setShippingaddress(address);
		orders.setShippingmethod(null);
		orders.setShippingcharges(null);
		orders.setOrdertotal(orders.getSubtotal());
		return ordersRepository.save(orders);
	}

	@Override
	public Orders saveShippingMethod(String usersId, String shippingName) {
		Shipping shipping = shippingRepository.findByName(shippingName);
		Orders updatedOrder = null;
		if (null != shipping) {
			Orders orders = getPendingOrderByUsersId(usersId);
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
	public Orders addItem(AddItemBean addItemBean, String usersId, String currency) {
		Catentry catentry = catentryRepository.findByPartnumber(addItemBean.getPartnumber());
		Orders orders = null;
		if (null != catentry && null != usersId) {
			orders = getPendingOrderByUsersId(usersId);
			List<Items> items = new ArrayList<>();
			if (null == orders) {
				orders = new Orders();
				orders.setUsersId(usersId);
				orders.setOrdersId(UUID.randomUUID().toString());
				orders.setStatus(OrderConstants.ORDER_STATUS_PENDING);
			} else {
				items = orders.getItems();
			}
			items = getItemDetails(addItemBean, currency, catentry, items, true);
			orders.setItems(items);
			orders.setSubtotal(PriceFormattingUtil.formatPriceAsDouble(
					Double.valueOf(items.stream().mapToDouble(item -> item.getItemtotal()).sum())));
			orders.setShippingaddress(null);
			orders.setShippingcharges(null);
			orders.setOrdertype(null);
			orders.setShippingmethod(null);
			orders.setOrdertotal(PriceFormattingUtil.formatPriceAsDouble(
					Double.valueOf(items.stream().mapToDouble(item -> item.getItemtotal()).sum())));
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
		return PriceFormattingUtil.formatPriceAsDouble(itemTotal);
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
			items.setListprice(PriceFormattingUtil.formatPriceAsDouble(listPrice));

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
	public Orders updateBasket(AddItemBean addItemBean, String usersId, String currency) {
		Catentry catentry = catentryRepository.findByPartnumber(addItemBean.getPartnumber());
		Orders orders = null;
		if (null != catentry) {
			orders = getPendingOrderByUsersId(usersId);
			List<Items> items = new ArrayList<>();
			if (null == orders) {
				orders = new Orders();
				orders.setUsersId(usersId);
				orders.setStatus(OrderConstants.ORDER_STATUS_PENDING);
			} else {
				items = orders.getItems();
			}
			items = getItemDetails(addItemBean, currency, catentry, items, false);
			orders.setItems(items);

			orders.setSubtotal(PriceFormattingUtil.formatPriceAsDouble(
					Double.valueOf(items.stream().mapToDouble(item -> item.getItemtotal()).sum())));
			orders.setShippingaddress(null);
			orders.setShippingcharges(null);
			orders.setOrdertype(null);
			orders.setShippingmethod(null);
			orders.setOrdertotal(PriceFormattingUtil.formatPriceAsDouble(
					Double.valueOf(items.stream().mapToDouble(item -> item.getItemtotal()).sum())));
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
	public Orders deleteItem(String partnumber, String usersId, String currency) {
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
					orders.setSubtotal(PriceFormattingUtil.formatPriceAsDouble(
							Double.valueOf(orders.getItems().stream().mapToDouble(item -> item.getItemtotal()).sum())));
					orders.setShippingaddress(null);
					orders.setShippingcharges(null);
					orders.setOrdertype(null);
					orders.setShippingmethod(null);
					orders.setOrdertotal(PriceFormattingUtil.formatPriceAsDouble(
							Double.valueOf(orders.getItems().stream().mapToDouble(item -> item.getItemtotal()).sum())));
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

	@Override
	public String generateToken(String usersId, String originatedBy, String registerType) {
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(securityConfiguration.getJwtSecret());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		String audience = securityConfiguration.getJwtAudience();

		String subject = null;
		Date expiryDate = null;
		if (OrderConstants.USER_TYPE_REGISTER.equalsIgnoreCase(registerType)) {
			subject = usersId;
			expiryDate = generateExpirationDateForPayment();
		} else {
			registerType = OrderConstants.USER_TYPE_GUEST;
			expiryDate = generateGuestExpirationDate();
			subject = "GUESTUSER";
		}
		String token = Jwts.builder().setId(usersId).setIssuer("commerce").setSubject(subject).setAudience(audience)
				.setIssuedAt(new Date()).setExpiration(expiryDate).claim(OrderConstants.JWT_CLAIM_USER_ID, usersId)
				.claim(OrderConstants.JWT_CLAIM_REGISTER_TYPE, registerType)
				.claim(OrderConstants.JWT_CLAIM_ORIGINATED_BY, originatedBy).signWith(signatureAlgorithm, signingKey)
				.compact();
		return token;
	}

	private Date generateGuestExpirationDate() {
		long expiresIn = getExpiredIn();
		return Date.from(Instant.now().plus(expiresIn, ChronoUnit.DAYS));
	}

	private Date generateExpirationDateForPayment() {
		return Date.from(Instant.now().plus(1, ChronoUnit.MINUTES));
	}

	public int getExpiredIn() {
		return securityConfiguration.getJwtGuestExpirationTime();
	}

	@Override
	public Orders mergeOrders(String usersId, String guestUserId) {
		Orders guestOrder = getPendingOrderByUsersId(guestUserId);
		if (null != guestOrder && null != guestOrder.getItems() && guestOrder.getItems().size() > 0) {
			guestOrder.getItems().stream().forEach(item -> {
				AddItemBean addItemBean = new AddItemBean();
				addItemBean.setPartnumber(item.getPartnumber());
				addItemBean.setQuantity(item.getQuantity());
				addItem(addItemBean, usersId, OrderConstants.CURRENCY_UK);
			});
		}
		Orders orders = getPendingOrderByUsersId(usersId);
		return orders;
	}

	@Override
	public Orders confirmPaypalOrder(Orders orders, PaypalResponseBean paypalResponseBean) {

		if (paypalResponseBean.getState().equals(OrderConstants.PAYMENT_STATUS_APPROVED)) {
			PaypalPaymentBean paypalPaymentBean = orders.getPaypalPayment();
			paypalPaymentBean.setIntent(paypalResponseBean.getIntent());
			paypalPaymentBean.setStatus(paypalResponseBean.getState());
			paypalPaymentBean.setPaymentId(paypalResponseBean.getId());
			paypalResponseBean.getTransactions().stream().forEach(transaction -> {
				paypalPaymentBean.setAmount(transaction.getAmount().getTotal());
				paypalPaymentBean.setCurrency(transaction.getAmount().getCurrency());
			});

			paypalPaymentBean.setCartId(paypalResponseBean.getCart());

			PaypalPayerBean payerBean = new PaypalPayerBean();
			payerBean.setEmail(paypalResponseBean.getPayer().getPayer_info().getEmail());
			payerBean.setFirstname(paypalResponseBean.getPayer().getPayer_info().getFirst_name());
			payerBean.setLastname(paypalResponseBean.getPayer().getPayer_info().getLast_name());
			payerBean.setPayerId(paypalResponseBean.getPayer().getPayer_info().getPayer_id());
			payerBean.setStatus(paypalResponseBean.getPayer().getStatus());

			paypalPaymentBean.setPayer(payerBean);

			orders.setPaypalPayment(paypalPaymentBean);
			orders.setStatus(OrderConstants.ORDER_STATUS_COMPLETE);

			SimpleDateFormat dt = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
			Date date = new Date();
			orders.setTimeplaced(dt.format(date));
		}
		return ordersRepository.save(orders);
	}

	@Override
	public Orders confirmGlobalCollectOrder(Orders orders, GlobalCollectResponse globalCollectResponse) {

		if (globalCollectResponse.getStatus().equalsIgnoreCase(OrderConstants.GLOBALCOLLECT_STATUS_PAYMENT_CREATED)) {
			orders.setStatus(OrderConstants.ORDER_STATUS_COMPLETE);

			GlobalCollectResponse globalCollectPayment = orders.getGlobalCollectPayment();
			globalCollectPayment.setStatus(globalCollectResponse.getStatus());
			globalCollectPayment.setAmount(globalCollectResponse.getAmount());
			globalCollectPayment.setCardNumber(globalCollectResponse.getCardNumber());
			globalCollectPayment.setCardType(globalCollectResponse.getCardType());
			globalCollectPayment.setCurrency(globalCollectResponse.getCurrency());

			orders.setGlobalCollectPayment(globalCollectPayment);

			SimpleDateFormat dt = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
			Date date = new Date();
			orders.setTimeplaced(dt.format(date));
		}
		return ordersRepository.save(orders);
	}
}
