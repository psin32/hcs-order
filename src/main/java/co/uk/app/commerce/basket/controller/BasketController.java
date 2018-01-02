package co.uk.app.commerce.basket.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import co.uk.app.commerce.additem.bean.AddItemBean;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.service.OrdersService;

@RestController
@RequestMapping("/api/basket")
public class BasketController {

	@Autowired
	private OrdersService ordersService;

	@GetMapping
	public @ResponseBody Orders getBasketByUserId(HttpServletRequest request, HttpServletResponse response) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);
		String cookieValue = "0";
		if (null != orders) {
			cookieValue = String.valueOf(orders.getItems().stream().mapToInt(item -> item.getQuantity()).sum());
		}
		Cookie cookie = new Cookie(OrderConstants.BASKET_COUNT, cookieValue);
		cookie.setPath("/");
		response.addCookie(cookie);
		return orders;
	}

	@PatchMapping
	public ResponseEntity<?> updateBasket(@RequestBody AddItemBean addItemBean, HttpServletRequest request,
			HttpServletResponse response) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.updateBasket(addItemBean, usersId, OrderConstants.CURRENCY_UK);
		String cookieValue = "0";
		if (null != orders && null != orders.getItems()) {
			cookieValue = String.valueOf(orders.getItems().stream().mapToInt(item -> item.getQuantity()).sum());
		}
		Cookie cookie = new Cookie(OrderConstants.BASKET_COUNT, cookieValue);
		cookie.setPath("/");
		response.addCookie(cookie);
		return ResponseEntity.ok(orders);
	}

	@DeleteMapping(path = "/{partnumber}")
	public ResponseEntity<?> deleteItem(@PathVariable("partnumber") String partnumber, HttpServletRequest request,
			HttpServletResponse response) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.deleteItem(partnumber, usersId, OrderConstants.CURRENCY_UK);
		String cookieValue = "0";
		if (null != orders && null != orders.getItems()) {
			cookieValue = String.valueOf(orders.getItems().stream().mapToInt(item -> item.getQuantity()).sum());
		}
		Cookie cookie = new Cookie(OrderConstants.BASKET_COUNT, cookieValue);
		cookie.setPath("/");
		response.addCookie(cookie);
		return ResponseEntity.ok(orders);
	}
}
