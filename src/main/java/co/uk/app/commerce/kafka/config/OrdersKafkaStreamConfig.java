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
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.uk.app.commerce.user.bean.KafkaAddressResponse;
import co.uk.app.commerce.user.document.Address;
import co.uk.app.commerce.user.repository.AddressRepository;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class OrdersKafkaStreamConfig {

	@Value("${kafka.config.bootstrap.servers}")
	private String bootstrapServers;

	@Value("${kafka.config.address.application_id}")
	private String addressApplicationId;

	@Value("${kafka.config.schema.registry.url}")
	private String schemaRegistryURL;

	@Value("${kafka.config.state.store.directory}")
	private String stateStoreDir;

	@Value("${kafka.topic.address}")
	private String addressTopic;

	@Autowired
	private AddressRepository addressRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public StreamsConfig config() {
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
	public KStream<String, GenericRecord> kstream() throws Exception {
		final StreamsBuilder addressBuilder = new StreamsBuilder();
		KStream<String, GenericRecord> address = readAddressStream(addressBuilder);
		KafkaStreams addressStream = new KafkaStreams(addressBuilder.build(), config());
		addressStream.start();
		return address;
	}

	private KStream<String, GenericRecord> readAddressStream(final StreamsBuilder builder) {
		KStream<String, GenericRecord> addressStream = builder.stream(addressTopic);
		addressStream.print(Printed.toSysOut());
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
								}
								addressRepository.save(address);
							}
						} catch (Exception e) {
							throw new SerializationException(e);
						}

						KeyValue<String, GenericRecord> kvin = new KeyValue<String, GenericRecord>(key, value);
						return kvin;
					}
				});
		return addressStream;
	}
}
