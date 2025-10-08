package mtcc.board.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "board_article_count")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardArticleCount {
	@Id
	private Long boardId; // shard key
	private Long articleCount;

	public BoardArticleCount(Long boardId, Long articleCount) {
		this.boardId = boardId;
		this.articleCount = articleCount;
	}

	public static BoardArticleCount init(Long boardId, Long articleCount) {
		return new BoardArticleCount(boardId, articleCount);
	}
}
