package co.uk.app.commerce.payment.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;

import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping(path = "/paypal")
	public ResponseEntity<?> paypalPayment(HttpServletRequest request, HttpServletResponse response) {
		Long usersId = Long.valueOf(String.valueOf(request.getAttribute(OrderConstants.USER_ID)));
		Payment payment = paymentService.createPaypalPayment(usersId);
		if (null != payment) {
			for (Links links : payment.getLinks()) {
				if (links.getRel().equals("approval_url")) {
					return ResponseEntity.ok(links.getHref());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

}
