package mtcc.board.hotarticle.service.eventhandler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleUnlikedEventPayload;
import mtcc.board.hotarticle.repository.ArticleLikeCountRepository;
import mtcc.board.hotarticle.service.EventHandler;
import mtcc.board.hotarticle.util.TimeCalculatorUtils;

@Component
@RequiredArgsConstructor
public class ArticleUnLikedEventHandler implements EventHandler<ArticleUnlikedEventPayload> {
	private final ArticleLikeCountRepository articleLikeCountRepository;

	@Override
	public void handle(Event<ArticleUnlikedEventPayload> event) {
		ArticleUnlikedEventPayload payload = event.payload();
		articleLikeCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleLikeCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<ArticleUnlikedEventPayload> event) {
		return EventType.ARTICLE_UNLIKED.equals(event.type());
	}

	@Override
	public Long findArticleId(Event<ArticleUnlikedEventPayload> event) {
		return event.payload().getArticleId();
	}
}
