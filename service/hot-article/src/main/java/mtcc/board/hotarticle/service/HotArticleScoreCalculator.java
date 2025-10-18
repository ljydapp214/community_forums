package mtcc.board.hotarticle.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mtcc.board.hotarticle.repository.ArticleCommentCountRepository;
import mtcc.board.hotarticle.repository.ArticleLikeCountRepository;
import mtcc.board.hotarticle.repository.ArticleViewCountRepository;

@Component
@RequiredArgsConstructor
public class HotArticleScoreCalculator {
	private static final long ARTICLE_VIEW_COUNT_WEIGHT = 1;
	private static final long ARTICLE_COMMENT_COUNT_WEIGHT = 2;
	private static final long ARTICLE_LIKE_COUNT_WEIGHT = 3;

	private final ArticleLikeCountRepository articleLikeCountRepository;
	private final ArticleViewCountRepository articleViewCountRepository;
	private final ArticleCommentCountRepository articleCommentCountRepository;

	public long calculate(Long articleId) {
		Long likeCount = articleLikeCountRepository.read(articleId);
		Long commentCount = articleCommentCountRepository.read(articleId);
		Long viewCount = articleViewCountRepository.read(articleId);

		return likeCount * ARTICLE_LIKE_COUNT_WEIGHT
			+ commentCount * ARTICLE_COMMENT_COUNT_WEIGHT
			+ viewCount * ARTICLE_VIEW_COUNT_WEIGHT;
	}
}
