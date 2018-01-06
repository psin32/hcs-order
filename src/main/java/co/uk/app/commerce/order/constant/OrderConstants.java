package co.uk.app.commerce.order.constant;

public interface OrderConstants {

	String ADDRESS_ACTIVE_STATUS = "P";

	String REQUEST_HEADER_USER_ID = "USER_ID";
	
	String REQUEST_HEADER_GUEST_USER_ID = "GUEST_USER_ID";

	String REQUEST_HEADER_REGISTER_TYPE = "REGISTER_TYPE";

	String JWT_CLAIM_USER_ID = "userId";

	String JWT_CLAIM_REGISTER_TYPE = "registertype";

	String ADDRESS_SHIPPING_TYPE = "S";

	Integer NON_SELFADDRESS = 0;

	String ORDER_STATUS_PENDING = "P";

	String ORDER_STATUS_COMPLETE = "C";

	String SHIPPING_TYPE_UK = "UK";

	String SHIPPING_TYPE_NONUK = "NONUK";

	String COOKIE_BASKET_COUNT = "BASKET_COUNT";

	String COOKIE_TOKEN = "TOKEN";
	
	String COOKIE_REGISTER_TYPE = "REGISTER_TYPE";

	String CURRENCY_UK = "GBP";

	String PAYMENT_STATUS_CREATED = "created";

	String PAYMENT_STATUS_APPROVED = "approved";

	String USER_TYPE_GUEST = "G";

	String USER_TYPE_REGISTER = "R";
}
