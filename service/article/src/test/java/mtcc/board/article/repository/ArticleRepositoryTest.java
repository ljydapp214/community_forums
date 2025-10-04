package mtcc.board.article.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import mtcc.board.article.entity.Article;

@Slf4j
@SpringBootTest
class ArticleRepositoryTest {
	@Autowired
	private ArticleRepository articleRepository;

	@Test
	void testFindAll() {
		List<Article> articles = articleRepository.findAll(1, 1499970L, 30);
		log.info("articles.size(): {}", articles.size());
		for (Article article : articles) {
			log.info("article: {}", article);
		}
	}

	@Test
	void testCountByBoardId() {
		long count = articleRepository.countByBoardId(1, 10000L);
		log.info("count: {}", count);
	}

	@Test
	void testFindAllInfiniteScroll() {
		List<Article> articles = articleRepository.findAllInfiniteScroll(1L, 30L);
		for (Article article : articles) {
			log.info("articleId: {}", article.getArticleId());
		}

		Long lastArticleId = articles.getLast().getArticleId();
		log.info("lastArticleId: {}", lastArticleId);

		List<Article> nextArticles = articleRepository.findAllInfiniteScroll(1L, lastArticleId, 30L);
		for (Article article : nextArticles) {
			log.info("next articleId: {}", article.getArticleId());
		}
	}
}
