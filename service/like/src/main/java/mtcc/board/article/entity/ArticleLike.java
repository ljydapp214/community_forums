package mtcc.board.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mtcc.board.article.jpa.BaseEntity;

@Table(name = "article_like")
@Getter
@Entity
@ToString
@NoArgsConstructor
public class ArticleLike extends BaseEntity {
	@Id
	private Long articleLikeId;
	private Long articleId; // shard key
	private Long userId;

	public ArticleLike(
		Long articleLikeId,
		Long articleId,
		Long userId
	) {
		this.articleLikeId = articleLikeId;
		this.articleId = articleId;
		this.userId = userId;
	}

	public static ArticleLike create(
		Long articleLikeId,
		Long articleId,
		Long userId
	) {
		return new ArticleLike(articleLikeId, articleId, userId);
	}
}
