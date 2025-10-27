package mtcc.board.articleread.service.event.handler;

import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
	void handle(Event<T> payload);

	boolean supports(Event<T> event);
}
