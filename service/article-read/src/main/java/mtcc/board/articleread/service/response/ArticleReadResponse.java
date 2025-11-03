package mtcc.board.articleread.service.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.ToString;
import mtcc.board.articleread.repository.ArticleQueryModel;

@Getter
@ToString
public class ArticleReadResponse {
	private final Long articleId;
	private final String title;
	private final String content;
	private final Long boardId;
	private final Long writerId;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
	private final Long articleCommentCount;
	private final Long articleLikeCount;
	private final Long articleViewCount;

	public ArticleReadResponse(
		Long articleId,
		String title,
		String content,
		Long boardId,
		Long writerId,
		LocalDateTime createdAt,
		LocalDateTime modifiedAt,
		Long articleCommentCount,
		Long articleLikeCount,
		Long articleViewCount
	) {
		this.articleId = articleId;
		this.title = title;
		this.content = content;
		this.boardId = boardId;
		this.writerId = writerId;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.articleCommentCount = articleCommentCount;
		this.articleLikeCount = articleLikeCount;
		this.articleViewCount = articleViewCount;
	}

	public static ArticleReadResponse from(
		ArticleQueryModel articleQueryModel,
		Long viewCount
	) {
		return new ArticleReadResponse(
			articleQueryModel.getArticleId(),
			articleQueryModel.getTitle(),
			articleQueryModel.getContent(),
			articleQueryModel.getBoardId(),
			articleQueryModel.getWriterId(),
			articleQueryModel.getCreatedAt(),
			articleQueryModel.getModifiedAt(),
			articleQueryModel.getArticleCommentCount(),
			articleQueryModel.getArticleLikeCount(),
			viewCount
		);
	}
}
