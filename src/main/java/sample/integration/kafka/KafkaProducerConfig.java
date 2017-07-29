package sample.integration.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
	
	private static final String TEST_TOPIC1 = "TEST_TOPIC1";
	private static final String TEST_TOPIC2 = "TEST_TOPIC2";

	//@Bean
	public IntegrationFlow sendToKafkaFlow() {
		return f -> f
				.<String>split(p -> Stream.generate(() -> p).limit(101).iterator(), null)
				.publishSubscribeChannel(c -> c
						.subscribe(sf -> sf.handle(
								kafkaMessageHandler(producerFactory(),TEST_TOPIC1 )
										.timestampExpression("T(Long).valueOf('1487694048633')"),
								e -> e.id("kafkaProducer1")))
						.subscribe(sf -> sf.handle(
								kafkaMessageHandler(producerFactory(), TEST_TOPIC2)
										.timestamp(m -> 1487694048644L),
								e -> e.id("kafkaProducer2")))
				);
	}

	private KafkaProducerMessageHandlerSpec<Integer, String, ?> kafkaMessageHandler(
			ProducerFactory<Integer, String> producerFactory, String topic) {
		return Kafka
				.outboundChannelAdapter(producerFactory)
				.messageKey(m -> m
						.getHeaders()
						.get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
				.partitionId(m -> 10)
				.topicExpression("headers[kafka_topic] ?: '" + topic + "'")
				.configureKafkaTemplate(t -> t.id("kafkaTemplate:" + topic));
	}
	 
	/**
	 * 
	 * Description：获取配置
	 * Date：		2017年7月11日
	 * @author 		shaqf
	 */
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		// kafka.metadata.broker.list=10.16.0.214:9092,10.16.0.215:9092,10.16.0.216:9092
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.103:9092");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 4096);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 40960);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}

	/** 获取工厂 */
	public ProducerFactory<Integer, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	/** 注册实例 */
	@Bean
	public KafkaTemplate<Integer, String> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}
