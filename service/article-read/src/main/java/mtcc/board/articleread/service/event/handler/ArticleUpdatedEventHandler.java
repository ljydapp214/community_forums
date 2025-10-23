package mtcc.board.articleread.service.event.handler;

import java.time.Duration;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.articleread.repository.ArticleQueryModelRepository;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleUpdatedEventPayload;

@Component
@RequiredArgsConstructor
public class ArticleUpdatedEventHandler implements EventHandler<ArticleUpdatedEventPayload> {
	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<ArticleUpdatedEventPayload> event) {
		ArticleUpdatedEventPayload payload = event.payload();
		articleQueryModelRepository.read(payload.getArticleId())
			.ifPresent(articleQueryModel -> {
				articleQueryModel.updateBy(payload);
				articleQueryModelRepository.update(articleQueryModel, Duration.ofDays(1L));
			});
	}

	@Override
	public boolean supports(Event<ArticleUpdatedEventPayload> event) {
		return EventType.ARTICLE_UPDATED == event.type();
	}
}
