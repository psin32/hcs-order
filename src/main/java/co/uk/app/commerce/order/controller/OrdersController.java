package co.uk.app.commerce.order.controller;

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
import co.uk.app.commerce.order.service.OrdersService;

@RestController
@RequestMapping("/api/order")
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	@GetMapping(path = "/summary")
	public ResponseEntity<?> getShippingMethods(HttpServletRequest request) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@GetMapping(path = "/option/{type}")
	public ResponseEntity<?> selectDeliveryOption(@PathVariable(value = "type") OrderType orderType,
			HttpServletRequest request) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.saveDeliveryOption(usersId, orderType);
		if (null != orders) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@GetMapping(path = "/shipping/{type}")
	public ResponseEntity<?> selectShippingMethod(@PathVariable(value = "type") String shippingName,
			HttpServletRequest request) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.saveShippingMethod(usersId, shippingName);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/delivery/address")
	public ResponseEntity<?> selectDeliveryAddress(@RequestBody Address address, HttpServletRequest request) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.saveDeliveryAddress(usersId, address);
		if (null != orders) {
			return ResponseEntity.ok(orders);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@PostMapping(path = "/confirmation")
	public ResponseEntity<?> orderConfirmation(@RequestBody OrderConfirmationBean orderConfirmationBean,
			HttpServletRequest request, HttpServletResponse response) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.confirmOrder(usersId, orderConfirmationBean);
		if (null != orders) {
			if (null != orders.getStatus()
					&& orders.getStatus().equalsIgnoreCase(OrderConstants.ORDER_STATUS_COMPLETE)) {
				String cookieValue = "0";
				Cookie cookie = new Cookie(OrderConstants.BASKET_COUNT, cookieValue);
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
