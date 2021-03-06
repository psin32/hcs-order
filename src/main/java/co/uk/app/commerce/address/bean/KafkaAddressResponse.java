package co.uk.app.commerce.address.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import co.uk.app.commerce.address.document.Address;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaAddressResponse {

	private Address after;

	private Address before;

	public Address getAfter() {
		return after;
	}

	public void setAfter(Address after) {
		this.after = after;
	}

	public Address getBefore() {
		return before;
	}

	public void setBefore(Address before) {
		this.before = before;
	}

}
