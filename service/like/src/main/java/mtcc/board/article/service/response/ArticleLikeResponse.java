package mtcc.board.article.service.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.ToString;
import mtcc.board.article.entity.ArticleLike;

@Getter
@ToString
public class ArticleLikeResponse {
	private Long articleLikeId;
	private Long articleId;
	private Long userId;
	private LocalDateTime createdAt;

	public static ArticleLikeResponse from(ArticleLike articleLike) {
		ArticleLikeResponse response = new ArticleLikeResponse();
		response.articleLikeId = articleLike.getArticleLikeId();
		response.articleId = articleLike.getArticleId();
		response.userId = articleLike.getUserId();
		response.createdAt = articleLike.getCreatedAt();
		return response;
	}
}
