package co.uk.app.commerce.address.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.address.document.Address;

public interface AddressRepository extends MongoRepository<Address, String> {

	Address findByAddressId(Long addressId);

	Address findByUsersIdAndAddressId(String usersId, Long addressId);

	List<Address> findByUsersId(String usersId);

	List<Address> findByUsersIdAndStatusAndSelfaddress(String usersId, String status, Integer selfaddress);

	List<Address> findByUsersIdAndAddresstype(String usersId, String addresstype);
}
