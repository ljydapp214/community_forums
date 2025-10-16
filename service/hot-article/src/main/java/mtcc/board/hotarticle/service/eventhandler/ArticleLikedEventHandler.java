package mtcc.board.hotarticle.service.eventhandler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleLikedEventPayload;
import mtcc.board.hotarticle.repository.ArticleLikeCountRepository;
import mtcc.board.hotarticle.service.EventHandler;
import mtcc.board.hotarticle.util.TimeCalculatorUtils;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {
	private final ArticleLikeCountRepository articleLikeCountRepository;

	@Override
	public void handle(Event<ArticleLikedEventPayload> event) {
		ArticleLikedEventPayload payload = event.payload();
		articleLikeCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleLikeCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<ArticleLikedEventPayload> event) {
		return EventType.ARTICLE_LIKED.equals(event.type());
	}

	@Override
	public Long findArticleId(Event<ArticleLikedEventPayload> event) {
		return event.payload().getArticleId();
	}
}
