package co.uk.app.commerce.order.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping(path = "/delivery")
	public ResponseEntity<?> getShippingAddresses(HttpServletRequest request) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		return ResponseEntity.ok(ordersService.getActiveShippingAddresses(usersId));
	}

	@GetMapping(path = "/summary")
	public ResponseEntity<?> getShippingMethods(HttpServletRequest request) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.getOrderSummary(usersId);
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

	@InitBinder
	public void initBinder(final WebDataBinder webdataBinder) {
		webdataBinder.registerCustomEditor(OrderType.class, new OrderTypeConverter());
	}
}
