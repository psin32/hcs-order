package co.uk.app.commerce.order.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class OrdersSecurityConfiguration {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration.time}")
	private int jwtExpirationTime;

	@Value("${jwt.token.prefix}")
	private String jwtTokenPrefix;

	@Value("${jwt.header}")
	private String jwtHeader;

	@Value("${jwt.audience}")
	private String jwtAudience;

	public String getJwtSecret() {
		return jwtSecret;
	}

	public int getJwtExpirationTime() {
		return jwtExpirationTime;
	}

	public String getJwtTokenPrefix() {
		return jwtTokenPrefix;
	}

	public String getJwtHeader() {
		return jwtHeader;
	}

	public String getJwtAudience() {
		return jwtAudience;
	}
}
