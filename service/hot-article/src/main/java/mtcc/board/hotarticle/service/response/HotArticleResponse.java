package mtcc.board.hotarticle.service.response;

import java.time.LocalDateTime;

import mtcc.board.hotarticle.client.ArticleClient;

public record HotArticleResponse(Long articleId, String title, LocalDateTime createdAt) {

	public static HotArticleResponse from(ArticleClient.ArticleResponse articleResponse) {
		return new HotArticleResponse(
			articleResponse.getArticleId(),
			articleResponse.getTitle(),
			articleResponse.getCreatedAt()
		);
	}
}
