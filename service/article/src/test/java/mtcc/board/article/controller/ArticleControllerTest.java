package mtcc.board.article.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import mtcc.board.article.service.request.ArticleCreateRequest;
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
}