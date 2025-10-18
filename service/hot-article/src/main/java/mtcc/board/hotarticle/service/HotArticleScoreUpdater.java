package mtcc.board.hotarticle.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.event.Event;
import mtcc.board.common.event.EventPayload;
import mtcc.board.hotarticle.client.ArticleClient;
import mtcc.board.hotarticle.repository.ArticleCreatedTimeRepository;
import mtcc.board.hotarticle.repository.HotArticleListRepository;

@Component
@RequiredArgsConstructor
public class HotArticleScoreUpdater {
	private static final long HOT_ARTICLE_COUNT = 10;
	private static final Duration HOT_ARTICLE_TTL = Duration.ofDays(10);

	private final HotArticleListRepository hotArticleListRepository;
	private final HotArticleScoreCalculator hotArticleScoreCalculator;
	private final ArticleCreatedTimeRepository articleCreatedTimeRepository;
	private final ArticleClient articleClient;

	private boolean isArticleCreatedToday(LocalDateTime createdTime) {
		return createdTime != null && createdTime.toLocalDate().equals(LocalDate.now());
	}

	public void update(Event<EventPayload> event, EventHandler<EventPayload> eventHandler) {
		Long articleId = eventHandler.findArticleId(event);
		LocalDateTime createdTime = articleCreatedTimeRepository.read(articleId);

		if (!isArticleCreatedToday(createdTime)) {
			return;
		}

		eventHandler.handle(event);

		long score = hotArticleScoreCalculator.calculate(articleId);
		hotArticleListRepository.add(articleId, createdTime, score, HOT_ARTICLE_COUNT, HOT_ARTICLE_TTL);
	}
}
