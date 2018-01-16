package co.uk.app.commerce.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaypalPaymentBean {

	@JsonIgnore
	private String paymentId;

	@JsonIgnore
	private String status;

	@JsonIgnore
	private String intent;

	private PaypalPayerBean payer;

	@JsonIgnore
	private String cartId;

	@JsonIgnore
	private PaypalPayeeBean payee;

	private String currency;

	private String amount;

	@JsonIgnore
	private String createTime;

	@JsonIgnore
	private String updateTime;
}
