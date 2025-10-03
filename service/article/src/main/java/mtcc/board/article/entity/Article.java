package mtcc.board.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mtcc.board.article.jpa.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "article")
public class Article extends BaseEntity {
	@Id
	private Long articleId;
	private String title;
	private String content;
	private Long boardId;
	private Long writerId;

	@Builder
	public Article(
		Long articleId,
		String title,
		String content,
		Long boardId,
		Long writerId
	) {
		this.articleId = articleId;
		this.title = title;
		this.content = content;
		this.boardId = boardId;
		this.writerId = writerId;
	}

	public static Article create(
		Long articleId,
		String title,
		String content,
		Long boardId,
		Long writerId
	) {
		return Article.builder()
			.articleId(articleId)
			.title(title)
			.content(content)
			.boardId(boardId)
			.writerId(writerId)
			.build();
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}
}
