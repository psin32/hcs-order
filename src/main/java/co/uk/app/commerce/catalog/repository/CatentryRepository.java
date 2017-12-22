package co.uk.app.commerce.catalog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.catalog.document.Catentry;

public interface CatentryRepository extends MongoRepository<Catentry, String> {

	Catentry findByPartnumber(String partnumber);
}
