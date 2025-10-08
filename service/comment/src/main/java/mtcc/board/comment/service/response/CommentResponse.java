package mtcc.board.comment.service.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.ToString;
import mtcc.board.comment.entity.Comment;
import mtcc.board.comment.entity.CommentV2;

@Getter
@ToString
public class CommentResponse {
	private Long commentId;
	private String content;
	private Long parentCommentId;
	private Long articleId;
	private Long writerId;
	private Boolean deleted;
	private String path;
	private LocalDateTime createdAt;

	public static CommentResponse from(Comment comment) {
		CommentResponse response = new CommentResponse();
		response.commentId = comment.getCommentId();
		response.content = comment.getContent();
		response.parentCommentId = comment.getParentCommentId();
		response.articleId = comment.getArticleId();
		response.writerId = comment.getWriterId();
		response.deleted = comment.getDeleted();
		response.createdAt = comment.getCreatedAt();
		return response;
	}

	public static CommentResponse from(CommentV2 comment) {
		CommentResponse response = new CommentResponse();
		response.commentId = comment.getCommentId();
		response.content = comment.getContent();
		response.articleId = comment.getArticleId();
		response.writerId = comment.getWriterId();
		response.path = comment.getCommentPath().getPath();
		response.deleted = comment.getDeleted();
		response.createdAt = comment.getCreatedAt();
		return response;
	}
}
