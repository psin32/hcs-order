package co.uk.app.commerce.order.controller;

import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.order.bean.GlobalCollectResponse;
import co.uk.app.commerce.order.bean.OrderConfirmationBean;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.bean.PaypalPaymentBean;
import co.uk.app.commerce.order.bean.PaymentType;
import co.uk.app.commerce.order.bean.PaypalLinks;
import co.uk.app.commerce.order.bean.PaypalResponseBean;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.converter.OrderTypeConverter;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.service.OrdersService;

@RestController
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${payment.paypal.create.url}")
	private String paypalCreateUrl;

	@Value("${payment.paypal.get.url}")
	private String paypalGetUrl;

	@Value("${payment.paypal.execute.url}")
	private String paypalExecuteUrl;

	@Value("${payment.globalcollect.create.url}")
	private String globalCollectCreateUrl;

	@Value("${payment.globalcollect.getstatus.url}")
	private String globalCollectGetStatusUrl;

	@GetMapping(path = "/summary")
	public ResponseEntity<?> getOrderSummary(HttpServletRequest request) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/option/{type}")
	public ResponseEntity<?> selectDeliveryOption(@PathVariable(value = "type") OrderType orderType,
			HttpServletRequest request) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		Orders orders = ordersService.saveDeliveryOption(usersId, orderType);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/shipping/{type}")
	public ResponseEntity<?> selectShippingMethod(@PathVariable(value = "type") String shippingName,
			HttpServletRequest request) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		Orders orders = ordersService.saveShippingMethod(usersId, shippingName);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/delivery/address")
	public ResponseEntity<?> selectDeliveryAddress(@RequestBody Address address, HttpServletRequest request) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		Orders orders = ordersService.saveDeliveryAddress(usersId, address);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@GetMapping(path = "/details/{ordersId}")
	public ResponseEntity<?> orderDetails(@PathVariable(value = "ordersId") String ordersId, HttpServletRequest request,
			HttpServletResponse response) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		Orders orders = ordersService.getPlacedOrderByUsersIdAndOrdersId(usersId, ordersId);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@GetMapping(path = "/history")
	public ResponseEntity<?> orderHistory(HttpServletRequest request, HttpServletResponse response) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		Collection<Orders> orders = ordersService.getPlacedOrdersByUsersId(usersId);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/submit/globalcollect")
	public ResponseEntity<?> createGlobalCollect(HttpServletRequest request) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		String registerType = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_REGISTER_TYPE));
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + ordersService.generateToken(usersId,
				OrderConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION, registerType));

		HttpEntity<Orders> requestEntity = new HttpEntity<>(orders, headers);

		ResponseEntity<GlobalCollectResponse> response = restTemplate.exchange(globalCollectCreateUrl, HttpMethod.POST,
				requestEntity, GlobalCollectResponse.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			GlobalCollectResponse globalCollectResponse = response.getBody();
			if (null != globalCollectResponse) {

				orders.setPaymentType(PaymentType.GLOBALCOLLECT);

				orders.setGlobalCollectPayment(globalCollectResponse);

				ordersService.save(orders);

				if (null != globalCollectResponse.getHostedCheckoutId()) {
					return ResponseEntity.ok("https://payment." + globalCollectResponse.getPartialRedirectUrl());
				}
			}
		} else {
			ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/submit/globalcollect/{cardId}")
	public ResponseEntity<?> createGlobalCollectForToken(@PathVariable(value = "cardId") String cardId,
			HttpServletRequest request) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		String registerType = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_REGISTER_TYPE));
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + ordersService.generateToken(usersId,
				OrderConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION, registerType));

		HttpEntity<Orders> requestEntity = new HttpEntity<>(orders, headers);

		ResponseEntity<GlobalCollectResponse> response = restTemplate.exchange(globalCollectCreateUrl + "/" + cardId,
				HttpMethod.POST, requestEntity, GlobalCollectResponse.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			GlobalCollectResponse globalCollectResponse = response.getBody();
			if (null != globalCollectResponse) {

				orders.setPaymentType(PaymentType.GLOBALCOLLECT);

				orders.setGlobalCollectPayment(globalCollectResponse);

				ordersService.save(orders);

				if (null != globalCollectResponse.getHostedCheckoutId()) {
					return ResponseEntity.ok("https://payment." + globalCollectResponse.getPartialRedirectUrl());
				}
			}
		} else {
			ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/submit/paypal")
	public ResponseEntity<?> submitOrder(HttpServletRequest request) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		String registerType = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_REGISTER_TYPE));
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + ordersService.generateToken(usersId,
				OrderConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION, registerType));

		HttpEntity<Orders> requestEntity = new HttpEntity<>(orders, headers);

		ResponseEntity<PaypalResponseBean> paymentResponse = restTemplate.exchange(paypalCreateUrl, HttpMethod.POST,
				requestEntity, PaypalResponseBean.class);

		if (paymentResponse.getStatusCode() == HttpStatus.OK) {
			PaypalResponseBean payment = paymentResponse.getBody();
			if (null != payment) {

				orders.setPaymentType(PaymentType.PAYPAL);

				PaypalPaymentBean paymentBean = new PaypalPaymentBean();
				paymentBean.setPaymentId(payment.getId());
				paymentBean.setStatus(payment.getState());
				paymentBean.setIntent(payment.getIntent());

				orders.setPaypalPayment(paymentBean);

				ordersService.save(orders);

				for (PaypalLinks links : payment.getLinks()) {
					if (links.getRel().equals("approval_url")) {
						return ResponseEntity.ok(links.getHref());
					}
				}
			}
		} else {
			ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/confirmation")
	public ResponseEntity<?> orderConfirmation(@RequestBody OrderConfirmationBean orderConfirmationBean,
			HttpServletRequest request, HttpServletResponse response) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		String registerType = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_REGISTER_TYPE));
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + ordersService.generateToken(usersId,
				OrderConstants.REQUEST_ORIGINATED_BY_ORDER_APPLICATION, registerType));

		HttpEntity<Orders> requestEntity = new HttpEntity<>(orders, headers);

		if (null != orders) {
			if (null != orders.getPaymentType() && orders.getPaymentType().equals(PaymentType.PAYPAL)) {
				ResponseEntity<PaypalResponseBean> paymentResponse = restTemplate.exchange(
						paypalExecuteUrl + orderConfirmationBean.getPaymentId(), HttpMethod.POST, requestEntity,
						PaypalResponseBean.class);
				if (paymentResponse.getStatusCode() == HttpStatus.OK) {
					PaypalResponseBean paypalResponseBean = paymentResponse.getBody();
					orders = ordersService.confirmPaypalOrder(orders, paypalResponseBean);
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			} else if (null != orders.getPaymentType() && orders.getPaymentType().equals(PaymentType.GLOBALCOLLECT)) {
				ResponseEntity<GlobalCollectResponse> paymentResponse = restTemplate.exchange(
						globalCollectGetStatusUrl + orders.getOrdersId() + "/"
								+ orders.getGlobalCollectPayment().getHostedCheckoutId(),
						HttpMethod.POST, requestEntity, GlobalCollectResponse.class);
				if (paymentResponse.getStatusCode() == HttpStatus.OK) {
					GlobalCollectResponse globalCollectResponse = paymentResponse.getBody();
					orders = ordersService.confirmGlobalCollectOrder(orders, globalCollectResponse);
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			}
		}

		if (null != orders) {
			if (null != orders.getStatus()
					&& orders.getStatus().equalsIgnoreCase(OrderConstants.ORDER_STATUS_COMPLETE)) {
				String cookieValue = "0";
				Cookie cookie = new Cookie(OrderConstants.COOKIE_BASKET_COUNT, cookieValue);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@InitBinder
	public void initBinder(final WebDataBinder webdataBinder) {
		webdataBinder.registerCustomEditor(OrderType.class, new OrderTypeConverter());
	}
}
