package co.uk.app.commerce.user.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.user.document.Address;

public interface AddressRepository extends MongoRepository<Address, String> {

	Address findByAddressId(Long addressId);

	List<Address> findByUsersId(Long usersId);

	List<Address> findByUsersIdAndAddresstype(Long usersId, String addresstype);
}
