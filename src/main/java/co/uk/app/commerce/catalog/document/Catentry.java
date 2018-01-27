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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "catentry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
