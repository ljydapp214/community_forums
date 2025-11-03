package mtcc.board.common.outbox;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@Configuration
@ComponentScan("mtcc.board.common.outbox")
public class MessageRelayConfig {
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public KafkaTemplate<String, String> messageRelayKafkaTemplate() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			org.apache.kafka.common.serialization.StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			org.apache.kafka.common.serialization.StringSerializer.class);
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");

		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
	}

	@Bean
	public Executor messageRelayPublishEventExecutor() {
		ThreadFactory threadFactory = Thread.ofPlatform().name("mr-pub-event-").factory();

		return new ThreadPoolExecutor(
			20, 50, 30, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(100), threadFactory);
	}

	@Bean
	public ScheduledExecutorService messageRelayPublishPendingEventExecutor() {
		return Executors.newSingleThreadScheduledExecutor();
	}
}
