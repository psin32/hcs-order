package co.uk.app.commerce.address.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.address.document.Address;

public interface AddressRepository extends MongoRepository<Address, String> {

	Address findByAddressId(Long addressId);

	List<Address> findByUsersId(Long usersId);
	
	List<Address> findByUsersIdAndStatusAndSelfaddress(Long usersId, String status, Integer selfaddress);

	List<Address> findByUsersIdAndAddresstype(Long usersId, String addresstype);
}
