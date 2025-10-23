package mtcc.board.articleread.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import mtcc.board.articleread.service.response.ArticleReadPageResponse;
import mtcc.board.articleread.service.response.ArticleReadResponse;

class ArticleReadControllerTest {
	RestClient articleReadRestClient = RestClient.create("http://localhost:8080");
	RestClient articleRestClient = RestClient.create("http://localhost:8081");

	@Test
	void readTest() {
		ArticleReadResponse response = articleReadRestClient.get()
			.uri("/v1/articles/{articleId}", 234958554511450112L)
			.retrieve()
			.body(ArticleReadResponse.class);

		System.out.println("response = " + response);
	}

	@Test
	void readAllTest() {
		ArticleReadPageResponse response1 = articleReadRestClient.get()
			.uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".formatted(1L,
				// 3000L,
				1L,
				5))
			.retrieve()
			.body(ArticleReadPageResponse.class);

		System.out.println("response1.getArticleCount() = " + response1.getArticleCount());
		for (ArticleReadResponse article : response1.getArticles()) {
			System.out.println("article.getArticleId() = " + article.getArticleId());
		}

		ArticleReadPageResponse response2 = articleRestClient.get()
			.uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".formatted(1L,
				// 3000L,
				1L,
				5))
			.retrieve()
			.body(ArticleReadPageResponse.class);

		System.out.println("response2.getArticleCount() = " + response2.getArticleCount());
		for (ArticleReadResponse article : response2.getArticles()) {
			System.out.println("article.getArticleId() = " + article.getArticleId());
		}
	}

	@Test
	void readAllInfiniteScrollTest() {
		List<ArticleReadResponse> response1 = articleReadRestClient.get()
			.uri("/v1/articles/infinite-scroll?boardId=%s&pageSize=%s&lastArticleId=%s".formatted(1L, 5L,
				235007829059563520L))
			.retrieve()
			.body(new ParameterizedTypeReference<List<ArticleReadResponse>>() {
			});

		for (ArticleReadResponse response : response1) {
			System.out.println("response = " + response.getArticleId());
		}

		List<ArticleReadResponse> response2 = articleRestClient.get()
			.uri("/v1/articles/infinite-scroll?boardId=%s&pageSize=%s&lastArticleId=%s".formatted(1L, 5L,
				235007829059563520L))
			.retrieve()
			.body(new ParameterizedTypeReference<List<ArticleReadResponse>>() {
			});

		for (ArticleReadResponse response : response2) {
			System.out.println("response = " + response.getArticleId());
		}
	}
}