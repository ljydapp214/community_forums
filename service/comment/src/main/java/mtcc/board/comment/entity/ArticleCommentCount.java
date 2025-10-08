package mtcc.board.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "article_comment_count")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleCommentCount {
	@Id
	private Long articleId; // shard key
	private Long commentCount;

	public ArticleCommentCount(Long articleId, Long commentCount) {
		this.articleId = articleId;
		this.commentCount = commentCount;
	}

	public static ArticleCommentCount init(Long articleId, Long commentCount) {
		return new ArticleCommentCount(articleId, commentCount);
	}
}
