package mtcc.board.common.outbox.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mtcc.board.article.jpa.BaseEntity;
import mtcc.board.common.event.EventType;

@Table(name = "outbox")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox extends BaseEntity {
	@Id
	private Long outboxId;
	@Enumerated(EnumType.STRING)
	private EventType eventType;
	private String payload;
	private Long shardKey;
	private LocalDateTime createdAt;

	public Outbox(
		Long outboxId,
		EventType eventType,
		String payload,
		Long shardKey,
		LocalDateTime createdAt
	) {
		this.outboxId = outboxId;
		this.eventType = eventType;
		this.payload = payload;
		this.shardKey = shardKey;
		this.createdAt = createdAt;
	}

	public static Outbox create(
		Long outboxId,
		EventType eventType,
		String payload,
		Long shardKey
	) {
		return new Outbox(outboxId, eventType, payload, shardKey, LocalDateTime.now());
	}
}
