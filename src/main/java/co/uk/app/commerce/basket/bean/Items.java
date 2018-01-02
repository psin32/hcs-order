package co.uk.app.commerce.basket.bean;

import java.util.List;

import co.uk.app.commerce.order.util.PriceFormattingUtil;

public class Items {

	private String partnumber;

	private String name;

	private String image;

	private Double listprice;

	private String currency;

	private Double offerprice;

	private Integer quantity;

	private Double itemtotal;

	private String formattedListprice;

	private String formattedOfferprice;

	private String formattedItemtotal;

	private String url;

	private List<Promotion> promotions;

	public String getPartnumber() {
		return partnumber;
	}

	public void setPartnumber(String partnumber) {
		this.partnumber = partnumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Double getListprice() {
		return listprice;
	}

	public void setListprice(Double listprice) {
		this.listprice = listprice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getOfferprice() {
		return offerprice;
	}

	public void setOfferprice(Double offerprice) {
		this.offerprice = offerprice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getItemtotal() {
		return itemtotal;
	}

	public void setItemtotal(Double itemtotal) {
		this.itemtotal = itemtotal;
	}

	public List<Promotion> getPromotions() {
		return promotions;
	}

	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFormattedListprice() {
		formattedListprice = PriceFormattingUtil.formatPriceAsString(this.listprice);
		return formattedListprice;
	}

	public String getFormattedOfferprice() {
		formattedOfferprice = PriceFormattingUtil.formatPriceAsString(this.offerprice);
		return formattedOfferprice;
	}

	public String getFormattedItemtotal() {
		formattedItemtotal = PriceFormattingUtil.formatPriceAsString(this.itemtotal);
		return formattedItemtotal;
	}

}
