package co.uk.app.commerce.additem.controller;

import java.util.UUID;

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
import co.uk.app.commerce.order.security.OrdersSecurityConfiguration;
import co.uk.app.commerce.order.service.OrdersService;

@RestController
@RequestMapping("/item/add")
public class AddItemController {

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private OrdersSecurityConfiguration securityConfiguration;

	@PutMapping
	public ResponseEntity<Orders> addItem(@RequestBody AddItemBean addItemBean, HttpServletRequest request,
			HttpServletResponse response) {
		String usersId = null;
		if (null == request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID)) {
			usersId = UUID.randomUUID().toString();
		} else {
			usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		}
		Orders orders = ordersService.addItem(addItemBean, usersId, OrderConstants.CURRENCY_UK);
		if (null != orders) {
			Cookie cookie = new Cookie(OrderConstants.COOKIE_BASKET_COUNT,
					String.valueOf(orders.getItems().stream().mapToInt(item -> item.getQuantity()).sum()));
			cookie.setPath("/");
			response.addCookie(cookie);

			String authHeader = request.getHeader(securityConfiguration.getJwtHeader());
			if (authHeader == null) {
				String token = ordersService.generateToken(usersId);

				Cookie tokenCookie = new Cookie(OrderConstants.COOKIE_TOKEN, token);
				tokenCookie.setPath("/");
				response.addCookie(tokenCookie);

				Cookie registerTypeCookie = new Cookie(OrderConstants.COOKIE_REGISTER_TYPE,
						OrderConstants.USER_TYPE_GUEST);
				registerTypeCookie.setPath("/");
				response.addCookie(registerTypeCookie);
			}
		}
		return ResponseEntity.ok(orders);
	}
}
