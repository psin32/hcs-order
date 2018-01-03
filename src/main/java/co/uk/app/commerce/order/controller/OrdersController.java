package co.uk.app.commerce.order.controller;

import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.order.bean.OrderConfirmationBean;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.converter.OrderTypeConverter;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.exception.OrdersApplicationException;
import co.uk.app.commerce.order.service.OrdersService;

@RestController
@RequestMapping("/api/order")
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

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

	@PostMapping(path = "/confirmation")
	public ResponseEntity<?> orderConfirmation(@RequestBody OrderConfirmationBean orderConfirmationBean,
			HttpServletRequest request, HttpServletResponse response) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		Orders orders = null;
		try {
			orders = ordersService.confirmOrder(usersId, orderConfirmationBean);
		} catch (OrdersApplicationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

	@InitBinder
	public void initBinder(final WebDataBinder webdataBinder) {
		webdataBinder.registerCustomEditor(OrderType.class, new OrderTypeConverter());
	}
}
