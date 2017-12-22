package co.uk.app.commerce.order.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.order.document.Orders;

public interface OrdersRepository extends MongoRepository<Orders, String> {

	Orders findByUsersIdAndStatus(Long usersId, String status);
}
