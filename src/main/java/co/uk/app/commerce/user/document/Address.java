package co.uk.app.commerce.user.document;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "address")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Serializable {

	private static final long serialVersionUID = -5755601784422563642L;

	@Id
	private String id;
	
	@Field(value = "address_id")
	@JsonProperty("address_id")
	private Long addressId;

	@JsonProperty("users_id")
	private Long usersId;

	private String addresstype;

	private String status;

	private Integer isprimary;

	private Integer selfaddress;

	private String title;

	private String firstname;

	private String lastname;

	private String email1;

	private String email2;

	private String phone1;

	private String phone2;

	private String nickname;

	private String address1;

	private String address2;

	private String address3;

	private String city;

	private String state;

	private String zipcode;

	private String country;

	private String lastcreate = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SS").format(new Date());

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public Long getUsersId() {
		return usersId;
	}

	public void setUsersId(Long usersId) {
		this.usersId = usersId;
	}

	public String getAddresstype() {
		return addresstype;
	}

	public void setAddresstype(String addresstype) {
		this.addresstype = addresstype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getIsprimary() {
		return isprimary;
	}

	public void setIsprimary(Integer isprimary) {
		this.isprimary = isprimary;
	}

	public Integer getSelfaddress() {
		return selfaddress;
	}

	public void setSelfaddress(Integer selfaddress) {
		this.selfaddress = selfaddress;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLastcreate() {
		return lastcreate;
	}

	public void setLastcreate(String lastcreate) {
		this.lastcreate = lastcreate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Address [addressId=" + addressId + ", usersId=" + usersId + ", addresstype=" + addresstype + ", status="
				+ status + ", isprimary=" + isprimary + ", selfaddress=" + selfaddress + ", title=" + title
				+ ", firstname=" + firstname + ", lastname=" + lastname + ", email1=" + email1 + ", email2=" + email2
				+ ", phone1=" + phone1 + ", phone2=" + phone2 + ", nickname=" + nickname + ", address1=" + address1
				+ ", address2=" + address2 + ", address3=" + address3 + ", city=" + city + ", state=" + state
				+ ", zipcode=" + zipcode + ", country=" + country + "]";
	}
}
