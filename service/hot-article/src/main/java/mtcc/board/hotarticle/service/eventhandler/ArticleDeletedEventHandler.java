package mtcc.board.hotarticle.service.eventhandler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleDeletedEventPayload;
import mtcc.board.hotarticle.repository.ArticleCreatedTimeRepository;
import mtcc.board.hotarticle.repository.HotArticleListRepository;
import mtcc.board.hotarticle.service.EventHandler;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
	private final HotArticleListRepository hotArticleListRepository;
	private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

	@Override
	public void handle(Event<ArticleDeletedEventPayload> event) {
		ArticleDeletedEventPayload payload = event.payload();
		articleCreatedTimeRepository.delete(payload.getArticleId());
		hotArticleListRepository.remove(payload.getArticleId(), payload.getCreatedAt());
	}

	@Override
	public boolean supports(Event<ArticleDeletedEventPayload> event) {
		return EventType.ARTICLE_DELETED.equals(event.type());
	}

	@Override
	public Long findArticleId(Event<ArticleDeletedEventPayload> event) {
		return event.payload().getArticleId();
	}
}
