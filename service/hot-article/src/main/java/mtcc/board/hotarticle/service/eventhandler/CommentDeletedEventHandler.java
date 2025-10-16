package mtcc.board.hotarticle.service.eventhandler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.CommentDeletedEventPayload;
import mtcc.board.hotarticle.repository.ArticleCommentCountRepository;
import mtcc.board.hotarticle.service.EventHandler;
import mtcc.board.hotarticle.util.TimeCalculatorUtils;

@Component
@RequiredArgsConstructor
public class CommentDeletedEventHandler implements EventHandler<CommentDeletedEventPayload> {
	private final ArticleCommentCountRepository articleCommentCountRepository;

	@Override
	public void handle(Event<CommentDeletedEventPayload> event) {
		CommentDeletedEventPayload payload = event.payload();
		articleCommentCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleCommentCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<CommentDeletedEventPayload> event) {
		return EventType.COMMENT_DELETED.equals(event.type());
	}

	@Override
	public Long findArticleId(Event<CommentDeletedEventPayload> event) {
		return event.payload().getArticleId();
	}
}
