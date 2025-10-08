package mtcc.board.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mtcc.board.article.jpa.BaseEntity;

@Table(name = "article_like_count")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleLikeCount extends BaseEntity {
	@Id
	private Long articleId; // shard key
	private Long likeCount;
	@Version
	private Long version;

	public ArticleLikeCount(Long articleId, Long likeCount, Long version) {
		this.articleId = articleId;
		this.likeCount = likeCount;
		this.version = version;
	}

	public static ArticleLikeCount init(Long articleId, Long likeCount) {
		return new ArticleLikeCount(articleId, likeCount, null);
	}

	public void increase() {
		this.likeCount++;
	}

	public void decrease() {
		this.likeCount--;
	}
}
