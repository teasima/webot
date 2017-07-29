package sample.integration.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.advice.ErrorMessageSendingRecoverer;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.support.RawRecordHeaderErrorMessageStrategy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.retry.support.RetryTemplate;


@Configuration
@EnableKafka
public class KafkaConsumerConfig {
	private static final String TEST_TOPIC1 = null;

	@Bean
	public PollableChannel errorChannel() {
		return new QueueChannel();
	}
 
	@Bean
	public IntegrationFlow topic1ListenerFromKafkaFlow() {
		return IntegrationFlows
				.from(Kafka.messageDrivenChannelAdapter(consumerFactory(),
						KafkaMessageDrivenChannelAdapter.ListenerMode.record, Pattern.compile("TEST_TOPIC1"))
						.configureListenerContainer(c ->
								c.ackMode(AbstractMessageListenerContainer.AckMode.MANUAL)
										.id("topic1ListenerContainer"))
						.recoveryCallback(new ErrorMessageSendingRecoverer(errorChannel(),
								new RawRecordHeaderErrorMessageStrategy()))
						.retryTemplate(new RetryTemplate())
						.filterInRetry(true))
				.filter(Message.class, m ->
								m.getHeaders().get(org.springframework.kafka.support.KafkaHeaders.RECEIVED_MESSAGE_KEY, Integer.class) < 101,
						f -> f.throwExceptionOnRejection(true))
				.<String, String>transform(String::toUpperCase)
				.channel(c -> c.queue("listeningFromKafkaResults1"))
				.get();
	}
	
	 
	/**
	 * 
	 * Description：获取配置
	 * Date：		2017年7月11日
	 * @author 		shaqf
	 */
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		// kafka.metadata.broker.list=10.16.0.214:9092,10.16.0.215:9092,10.16.0.216:9092
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.100:9092");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "Agent-Server-1.0.2");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return props;
	}

	/** 获取工厂 */
	public ConsumerFactory<String, String> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}

	/** 获取实例 */
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(3);
		factory.getContainerProperties().setPollTimeout(3000);
		return factory;
	}
}
