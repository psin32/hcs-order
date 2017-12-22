package co.uk.app.commerce.catalog.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public enum CatentryType {
	PRODUCTBEAN,
	ITEMBEAN
}
