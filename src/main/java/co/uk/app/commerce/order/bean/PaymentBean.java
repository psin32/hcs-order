package co.uk.app.commerce.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PaymentBean {

	@JsonIgnore
	private String paymentId;

	@JsonIgnore
	private String status;

	@JsonIgnore
	private String intent;

	private PayerBean payer;

	@JsonIgnore
	private String cartId;

	@JsonIgnore
	private PayeeBean payee;

	@JsonIgnore
	private String createTime;

	@JsonIgnore
	private String updateTime;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public PayerBean getPayer() {
		return payer;
	}

	public void setPayer(PayerBean payer) {
		this.payer = payer;
	}

	public String getCartId() {
		return cartId;
	}

	public void setCartId(String cartId) {
		this.cartId = cartId;
	}

	public PayeeBean getPayee() {
		return payee;
	}

	public void setPayee(PayeeBean payee) {
		this.payee = payee;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
