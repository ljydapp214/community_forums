package mtccArticleViewCountRepositoryboard.hotarticle.service.eventhandler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleViewedEventPayload;
import mtcc.board.hotarticle.repository.ArticleViewCountRepository;
import mtcc.board.hotarticle.service.EventHandler;
import mtcc.board.hotarticle.util.TimeCalculatorUtils;

@Component
@RequiredArgsConstructor
public class ArticleViewedEventHandler implements EventHandler<ArticleViewedEventPayload> {
	private final ArticleViewCountRepository articleViewCountRepository;

	@Override
	public void handle(Event<ArticleViewedEventPayload> event) {
		ArticleViewedEventPayload payload = event.payload();
		articleViewCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleViewCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<ArticleViewedEventPayload> event) {
		return EventType.ARTICLE_VIEWED.equals(event.type());
	}

	@Override
	public Long findArticleId(Event<ArticleViewedEventPayload> event) {
		return event.payload().getArticleId();
	}
}
