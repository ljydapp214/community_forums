package mtcc.board.article.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mtcc.board.article.entity.Article;
import mtcc.board.article.repository.ArticleRepository;
import mtcc.board.article.service.request.ArticleCreateRequest;
import mtcc.board.article.service.request.ArticleUpdateRequest;
import mtcc.board.article.service.response.ArticlePageResponse;
import mtcc.board.article.service.response.ArticleResponse;
import mtcc.board.common.snowflake.Snowflake;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final Snowflake snowflake = new Snowflake();
	private final ArticleRepository articleRepository;

	@Transactional
	public ArticleResponse create(ArticleCreateRequest request) {
		Article article = articleRepository.save(
			Article.create(snowflake.nextId(), request.title(), request.content(), request.boardId(),
				request.writerId())
		);

		return ArticleResponse.from(article);
	}

	@Transactional
	public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
		Article article = articleRepository.findById(articleId).orElseThrow();
		article.update(request.getTitle(), request.getContent());

		return ArticleResponse.from(article);
	}

	public ArticleResponse read(Long articleId) {
		return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
	}

	@Transactional
	public void delete(Long articleId) {
		Article article = articleRepository.findById(articleId).orElseThrow();
		articleRepository.delete(article);
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
}
