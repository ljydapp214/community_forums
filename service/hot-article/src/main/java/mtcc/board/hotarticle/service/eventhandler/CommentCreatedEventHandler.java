package mtcc.board.hotarticle.service.eventhandler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.CommentCreatedEventPayload;
import mtcc.board.hotarticle.repository.ArticleCommentCountRepository;
import mtcc.board.hotarticle.service.EventHandler;
import mtcc.board.hotarticle.util.TimeCalculatorUtils;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventHandler implements EventHandler<CommentCreatedEventPayload> {
	private final ArticleCommentCountRepository articleCommentCountRepository;

	@Override
	public void handle(Event<CommentCreatedEventPayload> event) {
		CommentCreatedEventPayload payload = event.payload();
		articleCommentCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleCommentCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<CommentCreatedEventPayload> event) {
		return EventType.COMMENT_CREATED.equals(event.type());
	}

	@Override
	public Long findArticleId(Event<CommentCreatedEventPayload> event) {
		return event.payload().getArticleId();
	}
}
