package co.uk.app.commerce.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.uk.app.commerce.address.bean.KafkaAddressResponse;
import co.uk.app.commerce.address.document.Address;
import co.uk.app.commerce.address.repository.AddressRepository;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

//@Configuration
@EnableKafka
@EnableKafkaStreams
public class OrdersKafkaStreamConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrdersKafkaStreamConfig.class);

	@Value("${kafka.config.bootstrap.servers}")
	private String bootstrapServers;

	@Value("${kafka.config.address.application_id}")
	private String addressApplicationId;

	@Value("${kafka.config.basket.application_id}")
	private String basketApplicationId;

	@Value("${kafka.config.schema.registry.url}")
	private String schemaRegistryURL;

	@Value("${kafka.config.state.store.directory}")
	private String stateStoreDir;

	@Value("${kafka.topic.address}")
	private String addressTopic;

	@Value("${kafka.topic.basket}")
	private String basketTopic;

	@Autowired
	private AddressRepository addressRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public StreamsConfig configAddress() {
		Serde<String> stringSerde = Serdes.String();

		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, addressApplicationId);
		props.put(StreamsConfig.STATE_DIR_CONFIG, stateStoreDir);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, stringSerde.getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

		return new StreamsConfig(props);
	}

	@Bean
	public StreamsConfig configBasket() {
		Serde<String> stringSerde = Serdes.String();

		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, basketApplicationId);
		props.put(StreamsConfig.STATE_DIR_CONFIG, stateStoreDir);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, stringSerde.getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

		return new StreamsConfig(props);
	}

	@Bean
	public KStream<String, GenericRecord> kstreamAddress() throws Exception {
		final StreamsBuilder addressBuilder = new StreamsBuilder();
		KStream<String, GenericRecord> address = readAddressStream(addressBuilder);
		KafkaStreams addressStream = new KafkaStreams(addressBuilder.build(), configAddress());
		addressStream.start();
		return address;
	}

//	@Bean
//	public KStream<String, GenericRecord> kstreamBasket() throws Exception {
//		final StreamsBuilder basketBuilder = new StreamsBuilder();
//		KStream<String, GenericRecord> basket = readBasketStream(basketBuilder);
//		KafkaStreams basketStream = new KafkaStreams(basketBuilder.build(), configBasket());
//		basketStream.start();
//		return basket;
//	}

	private KStream<String, GenericRecord> readAddressStream(final StreamsBuilder builder) {
		KStream<String, GenericRecord> addressStream = builder.stream(addressTopic);
		addressStream
				.map(new KeyValueMapper<String, GenericRecord, KeyValue<? extends String, ? extends GenericRecord>>() {

					@Override
					public KeyValue<? extends String, ? extends GenericRecord> apply(String key, GenericRecord value) {
						JsonNode data;
						Address address = null;
						try {
							objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
							data = objectMapper.readTree(value.toString().getBytes());
							KafkaAddressResponse kafkaResponse = objectMapper.treeToValue(data,
									KafkaAddressResponse.class);
							if (null != kafkaResponse.getAfter()) {
								address = kafkaResponse.getAfter();
								Address savedAddress = addressRepository.findByAddressId(address.getAddressId());
								if (null != savedAddress) {
									address.setId(savedAddress.getId());
									LOGGER.info("Address update received for usersId - {}, and addressId - {}",
											address.getUsersId(), address.getAddressId());
								} else {
									LOGGER.info("New address created for usersId - {}, and addressId - {}",
											address.getUsersId(), address.getAddressId());
								}
								addressRepository.save(address);
							}
						} catch (Exception e) {
							LOGGER.error("Error occured while parsing address response", e);
							throw new SerializationException(e);
						}

						KeyValue<String, GenericRecord> kvin = new KeyValue<String, GenericRecord>(key, value);
						return kvin;
					}
				});
		return addressStream;
	}

//	private KStream<String, GenericRecord> readBasketStream(final StreamsBuilder builder) {
//		KStream<String, GenericRecord> basketStream = builder.stream(basketTopic);
//		basketStream.print(Printed.toSysOut());
//		basketStream
//				.map(new KeyValueMapper<String, GenericRecord, KeyValue<? extends String, ? extends GenericRecord>>() {
//
//					@Override
//					public KeyValue<? extends String, ? extends GenericRecord> apply(String key, GenericRecord value) {
//						JsonNode data;
//						Basket basket = null;
//						try {
//							objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//							if (null != value) {
//								data = objectMapper.readTree(value.toString().getBytes());
//								KafkaBasketResponse kafkaResponse = objectMapper.treeToValue(data,
//										KafkaBasketResponse.class);
//								if (null != kafkaResponse.getAfter()) {
//									basket = kafkaResponse.getAfter();
//								}
//								if (null == basket) {
//									basket = kafkaResponse.getPatch();
//								}
//								if (null != basket && OrderConstants.ORDER_STATUS_PENDING.equals(basket.getStatus())) {
//									Long usersId = Long.valueOf(basket.getUserId());
//									Orders orders = ordersRepository.findByUsersIdAndStatus(usersId,
//											OrderConstants.ORDER_STATUS_PENDING);
//									if (null != orders) {
//										LOGGER.info("Basket update received for usersId - {}", basket.getUserId());
//										orders.setItems(basket.getItems());
//										orders.setBasketId(basket.getBasketId());
//										orders.setSubtotal(basket.getBasketTotal());
//										if (null != orders.getShippingcharges()) {
//											orders.setOrdertotal(basket.getBasketTotal() + orders.getShippingcharges());
//										} else {
//											orders.setOrdertotal(basket.getBasketTotal());
//										}
//									} else {
//										LOGGER.info("New order request received usersId - {}", basket.getUserId());
//										orders = new Orders();
//										orders.setOrdersId(UUID.randomUUID().toString());
//										orders.setBasketId(basket.getBasketId());
//										orders.setUsersId(usersId);
//										orders.setItems(basket.getItems());
//										orders.setStatus(OrderConstants.ORDER_STATUS_PENDING);
//										orders.setSubtotal(basket.getBasketTotal());
//										orders.setOrdertotal(basket.getBasketTotal());
//									}
//									ordersRepository.save(orders);
//								}
//							}
//						} catch (Exception e) {
//							LOGGER.error("Error occured while parsing basket response", e);
//							throw new SerializationException(e);
//						}
//
//						KeyValue<String, GenericRecord> kvin = new KeyValue<String, GenericRecord>(key, value);
//						return kvin;
//					}
//				});
//		return basketStream;
//	}
}
