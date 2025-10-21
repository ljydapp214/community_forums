package mtcc.board.hotarticle.service;

import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
	void handle(Event<T> event);

	boolean supports(Event<T> event);

	Long findArticleId(Event<T> event);
}
