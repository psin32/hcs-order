package co.uk.app.commerce.order.util;

import java.text.DecimalFormat;

public class PriceFormattingUtil {

	private static DecimalFormat df = new DecimalFormat("#0.00");

	public static String formatPriceAsString(Double price) {
		String formattedPrice = null;
		if (null != price) {
			formattedPrice = df.format(price);
		}
		return formattedPrice;
	}

	public static Double formatPriceAsDouble(Double price) {
		Double formattedPrice = null;
		if (null != price) {
			formattedPrice = Double.valueOf(df.format(price));
		}
		return formattedPrice;
	}
}
