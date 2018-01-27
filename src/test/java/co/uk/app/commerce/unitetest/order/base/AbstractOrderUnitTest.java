package co.uk.app.commerce.unitetest.order.base;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import co.uk.app.commerce.order.application.OrderApplication;

@ActiveProfiles({ "test", "unit" })
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { OrderApplication.class, MongoTestConfig.class })
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class AbstractOrderUnitTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	protected void importJSON(String collection, String file) {
		try {
			for (Object line : FileUtils.readLines(new File(file), "utf8")) {
				mongoTemplate.save(line, collection);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not import file: " + file, e);
		}
	}

	protected void dropCollection(Class<?> entityClass) {
		mongoTemplate.dropCollection(entityClass);
	}
}
