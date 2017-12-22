package co.uk.app.commerce.basket.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import co.uk.app.commerce.basket.document.Basket;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaBasketResponse {

	private Basket after;

	private Basket patch;

	public Basket getPatch() {
		return patch;
	}

	public void setPatch(Basket patch) {
		this.patch = patch;
	}

	public Basket getAfter() {
		return after;
	}

	public void setAfter(Basket after) {
		this.after = after;
	}

}
