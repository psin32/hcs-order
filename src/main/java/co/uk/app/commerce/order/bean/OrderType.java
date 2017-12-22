package co.uk.app.commerce.order.bean;

import java.util.Arrays;

public enum OrderType {

	HOME("HOME"), COLLECT("COLLECT");

	private String value;

	private OrderType(String value) {
		this.value = value;
	}

	public static OrderType fromValue(String value) {
		for (OrderType category : values()) {
			if (category.value.equalsIgnoreCase(value)) {
				return category;
			}
		}
		throw new IllegalArgumentException(
				"Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
	}
}
