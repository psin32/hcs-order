package co.uk.app.commerce.unitetest.order.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import co.uk.app.commerce.additem.bean.AddItemBean;
import co.uk.app.commerce.catalog.document.Catentry;
import co.uk.app.commerce.order.constant.OrderConstants;
import co.uk.app.commerce.order.document.Orders;
import co.uk.app.commerce.order.service.OrdersService;
import co.uk.app.commerce.unitetest.order.base.AbstractOrderUnitTest;
import co.uk.app.commerce.unitetest.order.constant.OrdersTestConstant;

public class OrdersServiceUnitTest extends AbstractOrderUnitTest {

	@Autowired
	private OrdersService ordersService;

	@Before
	public void setup() {
		importJSON("catentry", "src/test/resources/catentry.json");
	}

	@Test
	public void testAddItemInNewOrder() throws Exception {
		Orders orders = addItem();
		assertThat(orders.getOrdersId()).isNotNull();
	}

	@Test
	public void testAddItemInExistingOrder() throws Exception {
		addItem();
		Orders orders = addItem();
		assertThat(orders.getOrdersId()).isNotNull();
		assertThat(orders.getItems().get(0).getQuantity()).isEqualTo(2);
	}

	@Test
	public void testUpdateBasket() throws Exception {
		addItem();

		AddItemBean addItemBean = new AddItemBean();
		addItemBean.setPartnumber(OrdersTestConstant.TEST_PARTNUMBER);
		addItemBean.setQuantity(5);

		String usersId = OrdersTestConstant.TEST_USER;
		String currency = OrderConstants.CURRENCY_UK;

		Orders orders = ordersService.updateBasket(addItemBean, usersId, currency);
		assertThat(orders.getOrdersId()).isNotNull();
		assertThat(orders.getItems().get(0).getQuantity()).isEqualTo(5);
	}

	@Test
	public void testDeleteItem() throws Exception {
		Orders orders = addItem();
		assertThat(orders.getOrdersId()).isNotNull();

		String usersId = OrdersTestConstant.TEST_USER;
		String currency = OrderConstants.CURRENCY_UK;

		orders = ordersService.deleteItem(OrdersTestConstant.TEST_PARTNUMBER, usersId, currency);
		assertThat(orders.getOrdersId()).isNull();
	}

	private Orders addItem() {
		AddItemBean addItemBean = new AddItemBean();
		addItemBean.setPartnumber(OrdersTestConstant.TEST_PARTNUMBER);
		addItemBean.setQuantity(1);

		String usersId = OrdersTestConstant.TEST_USER;
		String currency = OrderConstants.CURRENCY_UK;
		return ordersService.addItem(addItemBean, usersId, currency);
	}

	@After
	public void tearDown() {
		dropCollection(Orders.class);
		dropCollection(Catentry.class);

	}
}
