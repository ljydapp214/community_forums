package mtcc.board.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mtcc.board.article.jpa.BaseEntity;

@Table(name = "article_view_count")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleViewCount extends BaseEntity {
	@Id
	private Long articleId; // shard key
	private Long viewCount;

	public ArticleViewCount(Long articleId, Long viewCount) {
		this.articleId = articleId;
		this.viewCount = viewCount;
	}

	public static ArticleViewCount init(Long articleId, Long viewCount) {
		return new ArticleViewCount(articleId, viewCount);
	}
}
