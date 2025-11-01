package mtcc.board.article.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mtcc.board.article.entity.ArticleViewCount;
import mtcc.board.article.repository.ArticleViewCountBackUpRepository;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleViewedEventPayload;
import mtcc.board.common.outbox.OutboxEventPublisher;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackUpProcessor {
	private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;
	private final OutboxEventPublisher outboxEventPublisher;

	@Transactional
	public void backUp(Long articleId, Long viewCount) {
		int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);

		if (result == 0) {
			articleViewCountBackUpRepository.findById(articleId)
				.ifPresentOrElse(_ -> {
					},
					() -> articleViewCountBackUpRepository.save(ArticleViewCount.init(articleId, viewCount)));
		}

		outboxEventPublisher.publish(
			EventType.ARTICLE_VIEWED,
			ArticleViewedEventPayload.builder()
				.articleId(articleId)
				.articleViewCount(viewCount)
				.build(),
			articleId
		);
	}
}
