package co.uk.app.commerce.order.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.basket.bean.Items;
import co.uk.app.commerce.basket.bean.Promotion;
import co.uk.app.commerce.order.bean.OrderType;

public class Orders {

	@Id
	private String id;

	private String ordersId;

	private Long usersId;

	private String basketId;

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

	private List<Promotion> promotions;

	@CreatedDate
	private Date createddate;

	@LastModifiedDate
	private Date updateddate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrdersId() {
		return ordersId;
	}

	public void setOrdersId(String ordersId) {
		this.ordersId = ordersId;
	}

	public Long getUsersId() {
		return usersId;
	}

	public void setUsersId(Long usersId) {
		this.usersId = usersId;
	}

	public String getBasketId() {
		return basketId;
	}

	public void setBasketId(String basketId) {
		this.basketId = basketId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Address getShippingaddress() {
		return shippingaddress;
	}

	public void setShippingaddress(Address shippingaddress) {
		this.shippingaddress = shippingaddress;
	}

	public Address getBillingaddress() {
		return billingaddress;
	}

	public void setBillingaddress(Address billingaddress) {
		this.billingaddress = billingaddress;
	}

	public List<Items> getItems() {
		return items;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}

	public OrderType getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(OrderType ordertype) {
		this.ordertype = ordertype;
	}

	public String getShippingmethod() {
		return shippingmethod;
	}

	public void setShippingmethod(String shippingmethod) {
		this.shippingmethod = shippingmethod;
	}

	public Double getShippingcharges() {
		return shippingcharges;
	}

	public void setShippingcharges(Double shippingcharges) {
		this.shippingcharges = shippingcharges;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public Double getTotaldiscount() {
		return totaldiscount;
	}

	public void setTotaldiscount(Double totaldiscount) {
		this.totaldiscount = totaldiscount;
	}

	public Double getOrdertotal() {
		return ordertotal;
	}

	public void setOrdertotal(Double ordertotal) {
		this.ordertotal = ordertotal;
	}

	public List<Promotion> getPromotions() {
		return promotions;
	}

	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

	public Date getCreateddate() {
		return createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	public Date getUpdateddate() {
		return updateddate;
	}

	public void setUpdateddate(Date updateddate) {
		this.updateddate = updateddate;
	}
	
	@Override
	public String toString() {
		return "Orders [id=" + id + ", ordersId=" + ordersId + ", usersId=" + usersId + ", basketId=" + basketId
				+ ", status=" + status + ", shippingaddress=" + shippingaddress + ", billingaddress=" + billingaddress
				+ ", items=" + items + ", ordertype=" + ordertype + ", shippingmethod=" + shippingmethod
				+ ", shippingcharges=" + shippingcharges + ", subtotal=" + subtotal + ", totaldiscount=" + totaldiscount
				+ ", ordertotal=" + ordertotal + ", promotions=" + promotions + ", createddate=" + createddate
				+ ", updateddate=" + updateddate + "]";
	}

}