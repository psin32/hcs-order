package co.uk.app.commerce.payment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.api.payments.Transaction;
import com.paypal.api.payments.Transactions;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import co.uk.app.commerce.order.bean.PaymentBean;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.service.OrdersService;

@Component
public class PaymentServiceImpl implements PaymentService {

	@Value("${payment.paypal.configuration.client_id}")
	private String clientId;

	@Value("${payment.paypal.configuration.client_secret}")
	private String clientSecret;

	@Value("${payment.paypal.configuration.cancel.url}")
	private String cancelURL;

	@Value("${payment.paypal.configuration.return.url}")
	private String returnURL;

	@Autowired
	private OrdersService ordersService;

	@Override
	public Payment createPaypalPayment(Long usersId) {
		Payment createdPayment = null;
		Orders orders = ordersService.getPendingOrderByUsersId(usersId);

		String mode = "sandbox";

		try {
			APIContext apiContext = new APIContext(clientId, clientSecret, mode);

			Payer payer = new Payer();
			payer.setPaymentMethod("paypal");

			List<Item> listOfItem = setItems(orders);
			ShippingAddress shippingAddress = setShippingAddress(orders);

			ItemList itemList = new ItemList();
			itemList.setItems(listOfItem);
			itemList.setShippingAddress(shippingAddress);
			itemList.setShippingMethod(orders.getShippingmethod());

			Details details = new Details();
			details.setShipping(String.valueOf(orders.getShippingcharges()));
			details.setSubtotal(String.valueOf(orders.getSubtotal()));

			Amount amount = new Amount();
			amount.setCurrency(OrderConstants.CURRENCY_UK);
			amount.setTotal(String.valueOf(orders.getOrdertotal()));
			amount.setDetails(details);

			Transaction transaction = new Transaction();
			transaction.setDescription("Order details for order number - " + orders.getOrdersId());
			transaction.setAmount(amount);
			transaction.setInvoiceNumber(orders.getOrdersId());
			transaction.setItemList(itemList);

			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.add(transaction);

			RedirectUrls redirectUrls = new RedirectUrls();
			redirectUrls.setCancelUrl(cancelURL);
			redirectUrls.setReturnUrl(returnURL);

			Payment payment = new Payment();
			payment.setIntent("sale");
			payment.setPayer(payer);
			payment.setTransactions(transactions);
			payment.setRedirectUrls(redirectUrls);

			createdPayment = payment.create(apiContext);

			if (null != createdPayment
					&& createdPayment.getState().equalsIgnoreCase(OrderConstants.PAYMENT_STATUS_CREATED)) {
				PaymentBean paymentBean = orders.getPaypalPayment();

				if (null == paymentBean) {
					paymentBean = new PaymentBean();
				}

				paymentBean.setPaymentId(createdPayment.getId());
				paymentBean.setStatus(createdPayment.getState());
				paymentBean.setIntent(createdPayment.getIntent());
				paymentBean.setCreateTime(createdPayment.getCreateTime());
				paymentBean.setUpdateTime(createdPayment.getUpdateTime());

				orders.setPaypalPayment(paymentBean);
				ordersService.save(orders);
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return createdPayment;
	}

	@Override
	public Payment getPaypalPaymentDetails(Long usersId, String paymentId) {
		Payment payment = null;
		String mode = "sandbox";

		try {
			APIContext apiContext = new APIContext(clientId, clientSecret, mode);
			payment = Payment.get(apiContext, paymentId);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return payment;
	}

	private List<Item> setItems(Orders orders) {
		List<Item> listOfItem = new ArrayList<>();
		orders.getItems().stream().forEach(items -> {
			Item item = new Item();
			item.setName(items.getName());
			item.setSku(items.getPartnumber());
			item.setCurrency(OrderConstants.CURRENCY_UK);
			item.setPrice(String.valueOf(items.getListprice()));
			item.setQuantity(String.valueOf(items.getQuantity()));

			listOfItem.add(item);
		});
		return listOfItem;
	}

	private ShippingAddress setShippingAddress(Orders orders) {
		ShippingAddress shippingAddress = new ShippingAddress();
		String name = orders.getShippingaddress().getTitle() + " " + orders.getShippingaddress().getFirstname() + " "
				+ orders.getShippingaddress().getLastname();
		shippingAddress.setRecipientName(name);
		shippingAddress.setLine1(orders.getShippingaddress().getAddress1());
		shippingAddress.setLine2(orders.getShippingaddress().getAddress2());
		shippingAddress.setId(String.valueOf(orders.getShippingaddress().getAddressId()));
		shippingAddress.setPostalCode(orders.getShippingaddress().getZipcode());
		shippingAddress.setCity(orders.getShippingaddress().getCity());
		shippingAddress.setCountryCode("GB");
		return shippingAddress;
	}

	@Override
	public Payment executePayment(Payment payment, Orders orders) {

		APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
		Transactions transactions = new Transactions();

		Details details = new Details();
		details.setShipping(String.valueOf(orders.getShippingcharges()));
		details.setSubtotal(String.valueOf(orders.getSubtotal()));

		Amount amount = new Amount();
		amount.setCurrency(OrderConstants.CURRENCY_UK);
		amount.setTotal(String.valueOf(orders.getOrdertotal()));
		amount.setDetails(details);

		transactions.setAmount(amount);

		List<Transactions> transactionList = new ArrayList<Transactions>();
		transactionList.add(transactions);

		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(payment.getPayer().getPayerInfo().getPayerId());
		paymentExecution.setTransactions(transactionList);

		Payment updatedPayment = null;
		try {
			updatedPayment = payment.execute(apiContext, paymentExecution);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return updatedPayment;
	}
}
