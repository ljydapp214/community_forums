package mtcc.board.articleread.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleClient {
	private RestClient restClient;
	@Value("${endpoints.board-article-service.url}")
	private String articleServiceUrl;

	@PostConstruct
	public void init() {
		this.restClient = RestClient.create(articleServiceUrl);
	}

	public Optional<ArticleResponse> read(Long articleId) {
		try {
			ArticleResponse response = restClient.get()
				.uri("/v1/articles/{articleId}", articleId)
				.retrieve()
				.body(ArticleResponse.class);
			return Optional.ofNullable(response);
		} catch (Exception e) {
			log.error("[ArticleClient.read] articleId={}", articleId, e);
			return Optional.empty();
		}
	}

	public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
		try {
			ArticlePageResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/v1/articles")
					.queryParam("boardId", boardId)
					.queryParam("page", page)
					.queryParam("pageSize", pageSize)
					.build())
				.retrieve()
				.body(ArticlePageResponse.class);
			return response;
		} catch (Exception e) {
			log.error("[ArticleClient.readAll] boardId={}, page={}, pageSize={}", boardId, page, pageSize, e);
			return ArticlePageResponse.EMPTY;
		}
	}

	public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long pageSize) {
		try {
			return restClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/v1/articles/infinite-scroll")
					.queryParam("boardId", boardId)
					.queryParam("pageSize", pageSize)
					.queryParam("lastArticleId", lastArticleId)
					.build())
				.retrieve()
				.body(new ParameterizedTypeReference<>() {
				});
		} catch (Exception e) {
			log.error("[ArticleClient.readAll] boardId={}, pageSize={}, lastArticleId={}", boardId, pageSize,
				lastArticleId, e);
			return List.of();
		}
	}

	public long count(Long boardId) {
		try {
			return restClient.get()
				.uri("/v1/articles/boards/{boardId}/count", boardId)
				.retrieve()
				.body(Long.class);
		} catch (Exception e) {
			log.error("[ArticleClient.count] boardId={}", boardId, e);
			return 0L;
		}
	}

	@Getter
	public static class ArticleResponse {
		private Long articleId;
		private String title;
		private String content;
		private Long boardId;
		private Long writerId;
		private LocalDateTime createdAt;
		private LocalDateTime modifiedAt;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ArticlePageResponse {
		// 페이징 조회 에러 발생 시 빈 페이지 응답 객체 반환
		public static ArticlePageResponse EMPTY = new ArticlePageResponse(List.of(), 0L);
		private List<ArticleResponse> articles;
		private Long articleCount;
	}
}
