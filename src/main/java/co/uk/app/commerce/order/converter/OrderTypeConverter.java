package co.uk.app.commerce.order.converter;

import java.beans.PropertyEditorSupport;

import co.uk.app.commerce.order.bean.OrderType;

public class OrderTypeConverter extends PropertyEditorSupport {

	public void setAsText(final String text) throws IllegalArgumentException {
		setValue(OrderType.fromValue(text));
	}
}
