package co.uk.app.commerce.user.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import co.uk.app.commerce.user.document.Users;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaUsersResponse {

	private Users after;

	private Users before;

	public Users getAfter() {
		return after;
	}

	public void setAfter(Users after) {
		this.after = after;
	}

	public Users getBefore() {
		return before;
	}

	public void setBefore(Users before) {
		this.before = before;
	}

}
