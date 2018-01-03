package co.uk.app.commerce.payment.controller;

import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ingenico.connect.gateway.sdk.java.Client;
import com.ingenico.connect.gateway.sdk.java.CommunicatorConfiguration;
import com.ingenico.connect.gateway.sdk.java.Factory;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.AmountOfMoney;
import com.ingenico.connect.gateway.sdk.java.domain.definitions.Card;
import com.ingenico.connect.gateway.sdk.java.domain.payment.CreatePaymentRequest;
import com.ingenico.connect.gateway.sdk.java.domain.payment.CreatePaymentResponse;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.CardPaymentMethodSpecificInput;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Customer;
import com.ingenico.connect.gateway.sdk.java.domain.payment.definitions.Order;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;

import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.exception.OrdersApplicationException;
import co.uk.app.commerce.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping(path = "/paypal")
	public ResponseEntity<?> paypalPayment(HttpServletRequest request, HttpServletResponse response) {

		Client client = null;
		try {
			client = getClient();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		CreatePaymentRequest body = new CreatePaymentRequest();

		// Such as the amount
		Order order = new Order();

		// Here the card information is set.
		AmountOfMoney amountOfMoney = new AmountOfMoney();
		amountOfMoney.setAmount(100L);
		amountOfMoney.setCurrencyCode("GBP");
		order.setAmountOfMoney(amountOfMoney);

		Customer customer = new Customer();
		customer.setLocale("en_US");

		com.ingenico.connect.gateway.sdk.java.domain.definitions.Address billingAddress = new com.ingenico.connect.gateway.sdk.java.domain.definitions.Address();
		billingAddress.setCountryCode("US");
		customer.setBillingAddress(billingAddress);

		order.setCustomer(customer);

		body.setOrder(order);

		CardPaymentMethodSpecificInput cardPaymentMethodSpecificInput = new CardPaymentMethodSpecificInput();
		cardPaymentMethodSpecificInput.setPaymentProductId(1);

		Card card = new Card();
		card.setCvv("123");
		card.setCardNumber("4567350000427977");
		card.setExpiryDate("1220");
		cardPaymentMethodSpecificInput.setCard(card);

		body.setCardPaymentMethodSpecificInput(cardPaymentMethodSpecificInput);

		CreatePaymentResponse paymentresponse = client.merchant("1156").payments().create(body);
		System.out.println(paymentresponse.toString());

		String usersId = String.valueOf(request.getAttribute(OrderConstants.REQUEST_HEADER_USER_ID));
		Payment payment = null;
		try {
			payment = paymentService.createPaypalPayment(usersId);
		} catch (OrdersApplicationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		if (null != payment) {
			for (Links links : payment.getLinks()) {
				if (links.getRel().equals("approval_url")) {
					return ResponseEntity.ok(links.getHref());
				}
			}
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	private Client getClient() throws URISyntaxException {
		String apiKeyId = System.getProperty("connect.api.apiKeyId", "26db202c9d925aeb");
		String secretApiKey = System.getProperty("connect.api.secretApiKey", "m3JSOs2qIrRkphTQz7ntpwy+fQnHVD/sRmNPh6IgBHE=");

		URL propertiesUrl = getClass().getResource("/example-configuration.properties");
		CommunicatorConfiguration configuration = Factory.createConfiguration(propertiesUrl.toURI(), apiKeyId,
				secretApiKey);
		return Factory.createClient(configuration);
	}

}
