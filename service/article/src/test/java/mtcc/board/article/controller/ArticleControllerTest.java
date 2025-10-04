package mtcc.board.article.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import mtcc.board.article.service.request.ArticleCreateRequest;
import mtcc.board.article.service.response.ArticlePageResponse;
import mtcc.board.article.service.response.ArticleResponse;

class ArticleControllerTest {
	RestClient restClient = RestClient.create("http://localhost:8080");

	@Test
	void createTest() {
		ArticleCreateRequest request = new ArticleCreateRequest("hi", "my content", 1L, 1L);

		ArticleResponse respnse = restClient.post()
			.uri("/v1/articles")
			.body(request)
			.retrieve()
			.body(ArticleResponse.class);
	}

	@Test
	void readAllTest() {
		ArticlePageResponse body = restClient.get()
			.uri("/v1/articles?boardId=1&page=50000&pageSize=30")
			.retrieve()
			.body(ArticlePageResponse.class);
		System.out.println("response.getArticleCount() = " + body.getArticleCount());
		for (ArticleResponse response : body.getArticles()) {
			System.out.println("article = " + response.getArticleId());
		}
	}

	@Test
	void readAllInfiniteScrollTest() {
		List<ArticleResponse> response = restClient.get()
			.uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
			.retrieve()
			.body(new ParameterizedTypeReference<List<ArticleResponse>>() {
			});
		System.out.println("first page");
		for (ArticleResponse articleResponse : response) {
			System.out.println("articleResponse.getArticleId = " + articleResponse.getArticleId());
		}

		Long lastArticleId = response.getLast().getArticleId();

		List<ArticleResponse> response2 = restClient.get()
			.uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=" + lastArticleId)
			.retrieve()
			.body(new ParameterizedTypeReference<List<ArticleResponse>>() {
			});
		System.out.println("second page");
		for (ArticleResponse articleResponse : response2) {
			System.out.println("articleResponse.getArticleId = " + articleResponse.getArticleId());
		}
	}
}