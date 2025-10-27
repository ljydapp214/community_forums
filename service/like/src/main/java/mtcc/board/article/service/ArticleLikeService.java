package mtcc.board.article.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mtcc.board.article.entity.ArticleLike;
import mtcc.board.article.entity.ArticleLikeCount;
import mtcc.board.article.repository.ArticleLikeCountRepository;
import mtcc.board.article.repository.ArticleLikeRepository;
import mtcc.board.article.service.response.ArticleLikeResponse;
import mtcc.board.common.event.EventType;
import mtcc.board.common.event.payload.ArticleLikedEventPayload;
import mtcc.board.common.event.payload.ArticleUnlikedEventPayload;
import mtcc.board.common.outbox.OutboxEventPublisher;
import mtcc.board.common.snowflake.Snowflake;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
	private final Snowflake snowflake = new Snowflake();
	private final ArticleLikeRepository articleLikeRepository;
	private final ArticleLikeCountRepository articleLikeCountRepository;
	private final OutboxEventPublisher outboxEventPublisher;

	public ArticleLikeResponse read(
		Long articleId, Long userId
	) {
		return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
			.map(ArticleLikeResponse::from)
			.orElseThrow();
	}

	public Long count(Long articleId) {
		return articleLikeCountRepository.findById(articleId)
			.map(ArticleLikeCount::getLikeCount)
			.orElse(0L);
	}

	@Transactional
	public void likePessimisticLock1(
		Long articleId, Long userId
	) {
		ArticleLike articleLike = ArticleLike.create(snowflake.nextId(), articleId, userId);
		articleLikeRepository.save(articleLike);

		int result = articleLikeCountRepository.increase(articleId);
		if (result == 0) {
			// 최초 요청 시에는 update 되는 레코드가 없으므로 1로 초기화
			// 트래픽이 순식간에 몰릴 경우 데이터 유실 가능성이 있음 -> 게시글 생성 시점에 미리 0 으로 초기하는 방식도 있음
			ArticleLikeCount articleLikeCount = ArticleLikeCount.init(articleId, 1L);
			articleLikeCountRepository.save(articleLikeCount);
		}

		outboxEventPublisher.publish(
			EventType.ARTICLE_LIKED,
			ArticleLikedEventPayload.builder()
				.articleLikeId(articleLike.getArticleLikeId())
				.articleId(articleLike.getArticleId())
				.userId(articleLike.getUserId())
				.createdAt(articleLike.getCreatedAt())
				.articleLikeCount(count(articleLike.getArticleId()))
				.build(),
			articleLike.getArticleId()
		);
	}

	@Transactional
	public void likePessimisticLock2(
		Long articleId, Long userId
	) {
		ArticleLike articleLike = ArticleLike.create(snowflake.nextId(), articleId, userId);
		articleLikeRepository.save(articleLike);

		ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
			.orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
		articleLikeCount.increase();
		articleLikeCountRepository.save(articleLikeCount);
	}

	@Transactional
	public void likeOptimisticLock(
		Long articleId, Long userId
	) {
		ArticleLike articleLike = ArticleLike.create(snowflake.nextId(), articleId, userId);
		articleLikeRepository.save(articleLike);

		ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId)
			.orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
		articleLikeCount.increase();
		articleLikeCountRepository.save(articleLikeCount);
	}

	@Transactional
	public void unlikePessimisticLock1(
		Long articleId, Long userId
	) {
		ArticleLike articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId).orElseThrow();
		articleLikeRepository.delete(articleLike);

		articleLikeCountRepository.decrease(articleId);

		outboxEventPublisher.publish(
			EventType.ARTICLE_UNLIKED,
			ArticleUnlikedEventPayload.builder()
				.articleLikeId(articleLike.getArticleLikeId())
				.articleId(articleLike.getArticleId())
				.userId(articleLike.getUserId())
				.createdAt(articleLike.getCreatedAt())
				.articleLikeCount(count(articleLike.getArticleId()))
				.build(),
			articleLike.getArticleId()
		);
	}

	@Transactional
	public void unlikePessimisticLock2(
		Long articleId, Long userId
	) {
		ArticleLike articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId).orElseThrow();
		articleLikeRepository.delete(articleLike);

		ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
			.orElseThrow();
		articleLikeCount.decrease();
	}

	@Transactional
	public void unlikeOptimisticLock(
		Long articleId, Long userId
	) {
		ArticleLike articleLike = articleLikeRepository.findByArticleIdAndUserId(articleId, userId).orElseThrow();
		articleLikeRepository.delete(articleLike);

		ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId).orElseThrow();
		articleLikeCount.decrease();
		articleLikeCountRepository.save(articleLikeCount);
	}
}
