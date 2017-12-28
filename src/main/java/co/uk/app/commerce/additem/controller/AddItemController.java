package co.uk.app.commerce.additem.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.uk.app.commerce.additem.bean.AddItemBean;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.service.OrdersService;

@RestController
@RequestMapping("/api/item/add")
public class AddItemController {

	@Autowired
	private OrdersService ordersService;

	@PutMapping
	public ResponseEntity<Orders> addItem(@RequestBody AddItemBean addItemBean, HttpServletRequest request,
			HttpServletResponse response) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Orders orders = ordersService.addItem(addItemBean, usersId, OrderConstants.CURRENCY_UK);
		if (null != orders) {
			Cookie cookie = new Cookie(OrderConstants.BASKET_COUNT,
					String.valueOf(orders.getItems().stream().mapToInt(item -> item.getQuantity()).sum()));
			cookie.setPath("/");
			response.addCookie(cookie);
		}
		return ResponseEntity.ok(orders);
	}
}
