package co.uk.app.commerce.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PaypalPayerBean {

	@JsonIgnore
	private String payerId;

	private String email;

	@JsonIgnore
	private String firstname;

	@JsonIgnore
	private String lastname;

	private String status;

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
