package co.uk.app.commerce.user.document;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import co.uk.app.commerce.user.bean.Address;

@Document(collection = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Users implements Serializable {

	private static final long serialVersionUID = 6868035278177368536L;

	@Id
	private String id;

	@Indexed(unique = true)
	@Field(value = "users_id")
	@JsonProperty("users_id")
	private Long usersId;

	private String username;

	private String registertype;

	private String profiletype;

	private Integer languageId;

	private List<Address> addresses;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUsersId() {
		return usersId;
	}

	public void setUsersId(Long usersId) {
		this.usersId = usersId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRegistertype() {
		return registertype;
	}

	public void setRegistertype(String registertype) {
		this.registertype = registertype;
	}

	public String getProfiletype() {
		return profiletype;
	}

	public void setProfiletype(String profiletype) {
		this.profiletype = profiletype;
	}

	public Integer getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Integer languageId) {
		this.languageId = languageId;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", usersId=" + usersId + ", username=" + username + ", registertype=" + registertype
				+ ", profiletype=" + profiletype + ", languageId=" + languageId + ", addresses=" + addresses + "]";
	}

}
