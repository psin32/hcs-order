package co.uk.app.commerce.payment.service;

import com.paypal.api.payments.Payment;

import co.uk.app.commerce.order.document.Orders;

public interface PaymentService {

	Payment createPaypalPayment(Long usersId);

	Payment getPaypalPaymentDetails(Long usersId, String paymentId);
	
	Payment executePayment(Payment payment, Orders orders);
}
