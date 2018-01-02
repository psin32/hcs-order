package co.uk.app.commerce.order.exception;

public class OrdersApplicationException extends Exception {

	private static final long serialVersionUID = -7115355011299208758L;

	public OrdersApplicationException() {
		super();
	}

	public OrdersApplicationException(String message) {
		super(message);
	}

	public OrdersApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public OrdersApplicationException(Throwable cause) {
		super(cause);
	}

	protected OrdersApplicationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
