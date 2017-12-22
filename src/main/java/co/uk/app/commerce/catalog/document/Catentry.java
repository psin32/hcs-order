package co.uk.app.commerce.catalog.document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import co.uk.app.commerce.catalog.bean.Association;
import co.uk.app.commerce.catalog.bean.Attributes;
import co.uk.app.commerce.catalog.bean.CatentryType;
import co.uk.app.commerce.catalog.bean.Description;
import co.uk.app.commerce.catalog.bean.Image;
import co.uk.app.commerce.catalog.bean.ListPrice;
import co.uk.app.commerce.catalog.bean.OfferPrice;

@Document(collection = "catentry")
public class Catentry {

	@Id
	private String id;

	@Indexed(unique = true)
	private String partnumber;

	private Description description;

	private List<Image> thumbnail;

	private List<Image> fullimage;

	private String mainimage;

	private CatentryType type;

	private Integer published;

	private Integer buyable;

	private String url;

	private String lastupdate = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SS").format(new Date());

	private String startdate;

	private String enddate;

	private List<Association> categories;

	private List<Association> childitems;

	private List<Attributes> attributes;

	private List<ListPrice> listprice;

	private List<OfferPrice> offerprice;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPartnumber() {
		return partnumber;
	}

	public void setPartnumber(String partnumber) {
		this.partnumber = partnumber;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public List<Image> getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(List<Image> thumbnail) {
		this.thumbnail = thumbnail;
	}

	public List<Image> getFullimage() {
		return fullimage;
	}

	public void setFullimage(List<Image> fullimage) {
		this.fullimage = fullimage;
	}

	public CatentryType getType() {
		return type;
	}

	public void setType(CatentryType type) {
		this.type = type;
	}

	public Integer getPublished() {
		return published;
	}

	public void setPublished(Integer published) {
		this.published = published;
	}

	public Integer getBuyable() {
		return buyable;
	}

	public void setBuyable(Integer buyable) {
		this.buyable = buyable;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(String lastupdate) {
		this.lastupdate = lastupdate;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public List<Association> getCategories() {
		return categories;
	}

	public void setCategories(List<Association> categories) {
		this.categories = categories;
	}

	public List<Association> getChilditems() {
		return childitems;
	}

	public void setChilditems(List<Association> childitems) {
		this.childitems = childitems;
	}

	public List<Attributes> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attributes> attributes) {
		this.attributes = attributes;
	}

	public List<ListPrice> getListprice() {
		return listprice;
	}

	public void setListprice(List<ListPrice> listprice) {
		this.listprice = listprice;
	}

	public List<OfferPrice> getOfferprice() {
		return offerprice;
	}

	public void setOfferprice(List<OfferPrice> offerprice) {
		this.offerprice = offerprice;
	}

	public String getMainimage() {
		return mainimage;
	}

	public void setMainimage(String mainimage) {
		this.mainimage = mainimage;
	}

}
