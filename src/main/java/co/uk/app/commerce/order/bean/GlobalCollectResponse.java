package co.uk.app.commerce.order.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalCollectResponse {

	private String hostedCheckoutId;

	private String partialRedirectUrl;

	private String paymentMethod;

	private String cardNumber;

	private String cardType;

	private String amount;

	private String currency;

	private String status;
}
