package co.uk.app.commerce.payment.service;

import com.paypal.api.payments.Payment;

import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.exception.OrdersApplicationException;

public interface PaymentService {

	Payment createPaypalPayment(String usersId) throws OrdersApplicationException;

	Payment getPaypalPaymentDetails(String usersId, String paymentId) throws OrdersApplicationException;

	Payment executePayment(Payment payment, Orders orders) throws OrdersApplicationException;
}
