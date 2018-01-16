package co.uk.app.commerce.order.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.basket.bean.Items;
import co.uk.app.commerce.basket.bean.Promotion;
import co.uk.app.commerce.order.bean.GlobalCollectResponse;
import co.uk.app.commerce.order.bean.OrderType;
import co.uk.app.commerce.order.bean.PaypalPaymentBean;
import co.uk.app.commerce.order.bean.PaymentType;
import co.uk.app.commerce.order.util.PriceFormattingUtil;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "orders")
@Getter
@Setter
public class Orders {

	@Id
	private String id;

	private String ordersId;

	private String usersId;

	private String status;

	private Address shippingaddress;

	private Address billingaddress;

	private List<Items> items;

	private OrderType ordertype;

	private String shippingmethod;

	private Double shippingcharges;

	private Double subtotal;

	private Double totaldiscount;

	private Double ordertotal;

	private String formattedShippingcharges;

	private String formattedSubtotal;

	private String formattedTotaldiscount;

	private String formattedOrdertotal;

	private List<Promotion> promotions;

	private PaymentType paymentType;

	private PaypalPaymentBean paypalPayment;

	private GlobalCollectResponse globalCollectPayment;

	@CreatedDate
	private Date createddate;

	@LastModifiedDate
	private Date updateddate;

	private String timeplaced;

	public String getFormattedShippingcharges() {
		formattedShippingcharges = PriceFormattingUtil.formatPriceAsString(this.shippingcharges);
		return formattedShippingcharges;
	}

	public String getFormattedSubtotal() {
		formattedSubtotal = PriceFormattingUtil.formatPriceAsString(this.subtotal);
		return formattedSubtotal;
	}

	public String getFormattedTotaldiscount() {
		formattedTotaldiscount = PriceFormattingUtil.formatPriceAsString(this.totaldiscount);
		return formattedTotaldiscount;
	}

	public String getFormattedOrdertotal() {
		formattedOrdertotal = PriceFormattingUtil.formatPriceAsString(this.ordertotal);
		return formattedOrdertotal;
	}
}
