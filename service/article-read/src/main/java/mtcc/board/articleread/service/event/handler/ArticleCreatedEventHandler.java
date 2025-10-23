package mtcc.board.articleread.service.event.handler;

import java.time.Duration;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.articleread.repository.ArticleIdListRepository;
import mtcc.board.articleread.repository.ArticleQueryModel;
import mtcc.board.articleread.repository.ArticleQueryModelRepository;
import mtcc.board.articleread.repository.BoardArticleCountRepository;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleCreatedEventPayload;

@Component
@RequiredArgsConstructor
public class ArticleCreatedEventHandler implements EventHandler<ArticleCreatedEventPayload> {
	private final ArticleQueryModelRepository articleQueryModelRepository;
	private final ArticleIdListRepository articleIdListRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;

	@Override
	public void handle(Event<ArticleCreatedEventPayload> event) {
		ArticleCreatedEventPayload payload = event.payload();
		articleQueryModelRepository.create(
			ArticleQueryModel.create(payload),
			Duration.ofDays(1L)
		);

		articleIdListRepository.add(payload.getBoardId(), payload.getArticleId(), 1000L);
		boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
	}

	@Override
	public boolean supports(Event<ArticleCreatedEventPayload> event) {
		return EventType.ARTICLE_CREATED == event.type();
	}
}
