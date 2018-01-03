package co.uk.app.commerce.order.repository;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.order.document.Orders;

import org.springframework.data.domain.Sort;

public interface OrdersRepository extends MongoRepository<Orders, String> {

	Collection<Orders> findByUsersIdAndStatus(String usersId, String status, Sort sort);

	Orders findByUsersIdAndStatusAndOrdersId(String usersId, String status, String ordersId);

	Orders findByUsersIdAndPaypalPaymentPaymentId(String usersId, String paymentId);
}
