package mtcc.board.articleread.service.event.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.articleread.repository.ArticleIdListRepository;
import mtcc.board.articleread.repository.ArticleQueryModelRepository;
import mtcc.board.articleread.repository.BoardArticleCountRepository;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleDeletedEventPayload;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
	private final ArticleQueryModelRepository articleQueryModelRepository;
	private final ArticleIdListRepository articleIdListRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;

	@Override
	public void handle(Event<ArticleDeletedEventPayload> event) {
		ArticleDeletedEventPayload payload = event.payload();
		// article list 에서 먼저 삭제 -> 사용자가 조회해도 삭제할 article 의 조회는 피할 수 있음
		articleIdListRepository.delete(payload.getBoardId(), payload.getArticleId());
		articleQueryModelRepository.delete(payload.getArticleId());
		boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
	}

	@Override
	public boolean supports(Event<ArticleDeletedEventPayload> event) {
		return EventType.ARTICLE_DELETED == event.type();
	}
}
