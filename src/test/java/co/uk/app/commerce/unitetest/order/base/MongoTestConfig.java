package co.uk.app.commerce.unitetest.order.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;

import cz.jirutka.spring.embedmongo.EmbeddedMongoBuilder;

@Configuration
public class MongoTestConfig extends AbstractMongoConfiguration {

	@Autowired
	private Environment env;

	@Override
	protected String getDatabaseName() {
		return env.getRequiredProperty("mongodb.database");
	}

	@Override
	public Mongo mongo() throws Exception {
		return new EmbeddedMongoBuilder().version("2.6.1").bindIp(env.getRequiredProperty("mongodb.host"))
				.port(Integer.valueOf(env.getRequiredProperty("mongodb.port"))).build();
	}
}
