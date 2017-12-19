package co.uk.app.commerce.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.user.document.Users;

public interface UsersRepository extends MongoRepository<Users, String> {

	Users findByUsersId(Long usersId);
	
	Users findByAddressesAddressId(Long addressId);
}
