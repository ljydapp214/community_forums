package mtcc.board.articleread.repository;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import mtcc.board.articleread.client.ArticleClient;
import mtcc.board.common.event.payload.ArticleCreatedEventPayload;
import mtcc.board.common.event.payload.ArticleLikedEventPayload;
import mtcc.board.common.event.payload.ArticleUnlikedEventPayload;
import mtcc.board.common.event.payload.ArticleUpdatedEventPayload;
import mtcc.board.common.event.payload.CommentCreatedEventPayload;
import mtcc.board.common.event.payload.CommentDeletedEventPayload;

@Getter
@NoArgsConstructor
public class ArticleQueryModel {
	private Long articleId;
	private String title;
	private String content;
	private Long boardId;
	private Long writerId;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private Long articleCommentCount;
	private Long articleLikeCount;

	public ArticleQueryModel(
		Long articleId,
		String title,
		String content,
		Long boardId,
		Long writerId,
		LocalDateTime createdAt,
		LocalDateTime modifiedAt,
		Long articleCommentCount,
		Long articleLikeCount
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
	}

	public static ArticleQueryModel create(ArticleCreatedEventPayload payload) {
		return new ArticleQueryModel(
			payload.getArticleId(),
			payload.getTitle(),
			payload.getContent(),
			payload.getBoardId(),
			payload.getWriterId(),
			payload.getCreatedAt(),
			payload.getModifiedAt(),
			0L,
			0L
		);
	}

	public static ArticleQueryModel create(
		ArticleClient.ArticleResponse article,
		Long commentCount,
		Long likeCount
	) {
		return new ArticleQueryModel(
			article.getArticleId(),
			article.getTitle(),
			article.getContent(),
			article.getBoardId(),
			article.getWriterId(),
			article.getCreatedAt(),
			article.getModifiedAt(),
			commentCount,
			likeCount
		);
	}

	public void updateBy(CommentCreatedEventPayload payload) {
		this.articleCommentCount = payload.getArticleCommentCount();
	}

	public void updateBy(CommentDeletedEventPayload payload) {
		this.articleCommentCount = payload.getArticleCommentCount();
	}

	public void updateBy(ArticleLikedEventPayload payload) {
		this.articleLikeCount = payload.getArticleLikeCount();
	}

	public void updateBy(ArticleUnlikedEventPayload payload) {
		this.articleLikeCount = payload.getArticleLikeCount();
	}

	public void updateBy(ArticleUpdatedEventPayload payload) {
		this.title = payload.getTitle();
		this.content = payload.getContent();
		this.boardId = payload.getBoardId();
		this.writerId = payload.getWriterId();
		this.createdAt = payload.getCreatedAt();
		this.modifiedAt = payload.getModifiedAt();
	}
}
