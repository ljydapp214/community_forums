package mtcc.board.article.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mtcc.board.article.entity.Article;
import mtcc.board.article.entity.BoardArticleCount;
import mtcc.board.article.repository.ArticleRepository;
import mtcc.board.article.repository.BoardArticleCountRepository;
import mtcc.board.article.service.request.ArticleCreateRequest;
import mtcc.board.article.service.request.ArticleUpdateRequest;
import mtcc.board.article.service.response.ArticlePageResponse;
import mtcc.board.article.service.response.ArticleResponse;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleCreatedEventPayload;
import mtcc.board.common.event.payload.ArticleDeletedEventPayload;
import mtcc.board.common.event.payload.ArticleUpdatedEventPayload;
import mtcc.board.common.outbox.OutboxEventPublisher;
import mtcc.board.common.snowflake.Snowflake;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final Snowflake snowflake = new Snowflake();
	private final ArticleRepository articleRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;
	private final OutboxEventPublisher outboxEventPublisher;

	@Transactional
	public ArticleResponse create(ArticleCreateRequest request) {
		Article article = Article.create(snowflake.nextId(), request.title(), request.content(), request.boardId(),
			request.writerId());
		Article saved = articleRepository.save(article);
		int result = boardArticleCountRepository.increase(request.boardId());
		if (result == 0) {
			boardArticleCountRepository.save(BoardArticleCount.init(request.boardId(), 1L));
		}

		outboxEventPublisher.publish(
			EventType.ARTICLE_CREATED,
			ArticleCreatedEventPayload.builder()
				.articleId(saved.getArticleId())
				.title(saved.getTitle())
				.content(saved.getContent())
				.boardId(saved.getBoardId())
				.writerId(saved.getWriterId())
				.createdAt(saved.getCreatedAt())
				.modifiedAt(saved.getModifiedAt())
				.boardArticleCount(count(saved.getBoardId()))
				.build(),
			saved.getBoardId()
		);

		return ArticleResponse.from(saved);
	}

	@Transactional
	public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
		Article article = articleRepository.findById(articleId).orElseThrow();
		article.update(request.getTitle(), request.getContent());

		outboxEventPublisher.publish(
			EventType.ARTICLE_UPDATED,
			ArticleUpdatedEventPayload.builder()
				.articleId(article.getArticleId())
				.title(article.getTitle())
				.content(article.getContent())
				.boardId(article.getBoardId())
				.writerId(article.getWriterId())
				.createdAt(article.getCreatedAt())
				.modifiedAt(article.getModifiedAt())
				.build(),
			article.getBoardId()
		);

		return ArticleResponse.from(article);
	}

	public ArticleResponse read(Long articleId) {
		return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
	}

	@Transactional
	public void delete(Long articleId) {
		Article article = articleRepository.findById(articleId).orElseThrow();
		articleRepository.delete(article);

		outboxEventPublisher.publish(
			EventType.ARTICLE_DELETED,
			ArticleDeletedEventPayload.builder()
				.articleId(article.getArticleId())
				.title(article.getTitle())
				.content(article.getContent())
				.boardId(article.getBoardId())
				.writerId(article.getWriterId())
				.createdAt(article.getCreatedAt())
				.modifiedAt(article.getModifiedAt())
				.boardArticleCount(count(article.getBoardId()))
				.build(),
			article.getBoardId()
		);

		boardArticleCountRepository.decrease(article.getBoardId());
	}

	public ArticlePageResponse readAll(long boardId, long page, long pageSize) {
		long offset = (page - 1) * pageSize;
		long limit = PageLimitCalculator.calculatePageLimit(page, pageSize, 10L);
		var articles = articleRepository.findAll(boardId, offset, pageSize).stream()
			.map(ArticleResponse::from)
			.toList();
		long articleCount = articleRepository.countByBoardId(boardId, limit);

		return ArticlePageResponse.of(articles, articleCount);
	}

	public List<ArticleResponse> readAllInfiniteScroll(long boardId, long lastArticleId, long limit) {
		List<Article> articles = lastArticleId == 0 ?
			articleRepository.findAllInfiniteScroll(boardId, limit)
			: articleRepository.findAllInfiniteScroll(boardId, lastArticleId, limit);

		return articles.stream().map(ArticleResponse::from).toList();
	}

	public Long count(Long boardId) {
		return boardArticleCountRepository.findById(boardId)
			.map(BoardArticleCount::getArticleCount)
			.orElse(0L);
	}
}
