package mtcc.board.common.outbox;

import mtcc.board.common.outbox.entity.Outbox;

public record OutboxEvent(Outbox outbox) {

	public static OutboxEvent of(Outbox outbox) {
		return new OutboxEvent(outbox);
	}
}
