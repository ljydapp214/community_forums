package mtcc.board.articleread.data;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class DataInitializer {
	RestClient articleServiceClient = RestClient.create("http://localhost:8081");
	RestClient commentServiceClient = RestClient.create("http://localhost:8082");
	RestClient likeServiceClient = RestClient.create("http://localhost:8084");
	RestClient viewServiceClient = RestClient.create("http://localhost:8085");

	@Test
	void initialize() {
		for (int i = 0; i < 30; i++) {
			Long articleId = createArticle();
			System.out.println("articleId = " + articleId);
			long commentCount = RandomGenerator.getDefault().nextLong(10);
			long likeCount = RandomGenerator.getDefault().nextLong(10);
			long viewCount = RandomGenerator.getDefault().nextLong(200);

			createComment(articleId, commentCount);
			like(articleId, likeCount);
			view(articleId, viewCount);
		}
	}

	Long createArticle() {
		return articleServiceClient.post()
			.uri("/v1/articles")
			.body(new ArticleCreateRequest("title", "content", 1L, 1L))
			.retrieve()
			.body(ArticleResponse.class)
			.getArticleId();
	}

	void createComment(Long articleId, long commentCount) {
		while (commentCount-- > 0) {
			commentServiceClient.post()
				.uri("/v2/comments")
				.body(new CommentCreateRequest(articleId, "content", 1L))
				.retrieve().toBodilessEntity();
		}
	}

	void like(Long articleId, long likeCount) {
		while (likeCount-- > 0) {
			likeServiceClient.post()
				.uri("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1", articleId, likeCount)
				.retrieve().toBodilessEntity();
		}
	}

	void view(Long articleId, long viewCount) {
		while (viewCount-- > 0) {
			viewServiceClient.post()
				.uri("/v1/article-views/articles/{articleId}/users/{userId}", articleId, viewCount)
				.retrieve().toBodilessEntity();
		}
	}

	@Getter
	@AllArgsConstructor
	static class ArticleCreateRequest {
		private String title;
		private String content;
		private Long writerId;
		private Long boardId;
	}

	@Getter
	static class ArticleResponse {
		private Long articleId;
	}

	@Getter
	@AllArgsConstructor
	static class CommentCreateRequest {
		private Long articleId;
		private String content;
		private Long writerId;
	}
}
