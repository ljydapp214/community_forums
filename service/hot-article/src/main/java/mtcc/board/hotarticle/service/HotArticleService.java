package mtcc.board.hotarticle.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventPayload;
import mtcc.board.common.event.EventType;
import mtcc.board.hotarticle.client.ArticleClient;
import mtcc.board.hotarticle.repository.HotArticleListRepository;
import mtcc.board.hotarticle.service.response.HotArticleResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleService {
	private final ArticleClient articleClient;
	private final List<EventHandler> eventHandlers;
	private final HotArticleScoreUpdater hotArticleScoreUpdater;
	private final HotArticleListRepository hotArticleListRepository;

	private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
		return eventHandlers.stream()
			.filter(handler -> handler.supports(event))
			.findFirst()
			.orElse(null);
	}

	private boolean isArticleCreatedOrDeleted(Event<EventPayload> event) {
		return EventType.ARTICLE_CREATED.equals(event.type())
			|| EventType.ARTICLE_DELETED.equals(event.type());
	}

	public void handleEvent(Event<EventPayload> event) {
		EventHandler<EventPayload> eventHandler = findEventHandler(event);
		if (eventHandler == null) {
			return;
		}

		if (isArticleCreatedOrDeleted(event)) {
			eventHandler.handle(event);
		} else {
			hotArticleScoreUpdater.update(event, eventHandler);
		}
	}

	public List<HotArticleResponse> readAll(String dateStr) {
		return hotArticleListRepository.readAll(dateStr)
			.stream()
			.map(articleClient::read)
			.filter(Objects::nonNull)
			.map(HotArticleResponse::from)
			.toList();
	}
}
