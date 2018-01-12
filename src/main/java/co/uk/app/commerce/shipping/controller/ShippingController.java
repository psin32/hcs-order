package co.uk.app.commerce.shipping.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.shipping.document.Shipping;
import co.uk.app.commerce.shipping.service.ShippingService;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

	@Autowired
	private ShippingService shippingService;

	@PostMapping
	public ResponseEntity<?> saveShippingMethods(@RequestBody Shipping shipping) {
		Shipping savedShipping = shippingService.save(shipping);
		if (null != savedShipping) {
			return ResponseEntity.ok(savedShipping);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@GetMapping
	public ResponseEntity<?> getShippingMethods(HttpServletRequest request) {
		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		List<Shipping> shipping = shippingService.getActiveShippingForCurrentOrder(usersId);
		if (null != shipping) {
			return ResponseEntity.ok(shipping);
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
}
