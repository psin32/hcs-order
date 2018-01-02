package co.uk.app.commerce.catalog.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import co.uk.app.commerce.order.util.PriceFormattingUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListPrice {

	private String currency;
	
	private Double price;

	private String formattedPrice;

	public String getFormattedPrice() {
		formattedPrice = PriceFormattingUtil.formatPriceAsString(this.price);
		return formattedPrice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
	
	
}
