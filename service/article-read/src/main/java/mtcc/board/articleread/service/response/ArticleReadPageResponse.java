package mtcc.board.articleread.service.response;

import java.util.List;

import lombok.Getter;

@Getter
public class ArticleReadPageResponse {
	private final List<ArticleReadResponse> articles;
	private final Long articleCount;

	public ArticleReadPageResponse(List<ArticleReadResponse> articles, Long articleCount) {
		this.articles = articles;
		this.articleCount = articleCount;
	}

	public static ArticleReadPageResponse of(List<ArticleReadResponse> articles, Long articleCount) {
		return new ArticleReadPageResponse(articles, articleCount);
	}
}
