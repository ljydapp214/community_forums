package mtcc.board.common.outbox;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventPayload;
import mtcc.board.common.event.EventType;
import mtcc.board.common.outbox.entity.Outbox;
import mtcc.board.common.snowflake.Snowflake;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
	private final Snowflake outboxSnowflake = new Snowflake();
	private final Snowflake eventIdSnowflake = new Snowflake();
	private final ApplicationEventPublisher applicationEventPublisher;

	public void publish(EventType type, EventPayload payload, Long shardKey) {
		// articleId = 10, shardKey == articleId
		// 10 % 4 = 2
		Outbox outbox = Outbox.create(
			outboxSnowflake.nextId(),
			type,
			Event
				.of(eventIdSnowflake.nextId(), type, payload)
				.toJson(),
			shardKey % MessageRelayConstants.SHARD_COUNT
		);

		applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
	}
}
