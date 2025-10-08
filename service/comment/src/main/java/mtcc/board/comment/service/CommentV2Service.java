package mtcc.board.comment.service;

import static java.util.function.Predicate.not;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mtcc.board.comment.entity.ArticleCommentCount;
import mtcc.board.comment.entity.CommentPath;
import mtcc.board.comment.entity.CommentV2;
import mtcc.board.comment.repository.ArticleCommentCountRepository;
import mtcc.board.comment.repository.CommentV2Repository;
import mtcc.board.comment.service.request.CommentCreateRequestV2;
import mtcc.board.comment.service.response.CommentPageResponse;
import mtcc.board.comment.service.response.CommentResponse;
import mtcc.board.common.snowflake.Snowflake;

@Service
@RequiredArgsConstructor
public class CommentV2Service {
	private final Snowflake snowflake = new Snowflake();
	private final CommentV2Repository commentV2Repository;
	private final ArticleCommentCountRepository articleCommentCountRepository;

	@Transactional
	public CommentResponse create(CommentCreateRequestV2 request) {
		CommentV2 parent = findParent(request);
		CommentPath parentCommentPath = parent == null ? CommentPath.create("") : parent.getCommentPath();

		String descendantsTopPath = commentV2Repository.findDescendantsTopPath(request.getArticleId(),
			parentCommentPath.getPath()).orElse(null);
		CommentPath childCommentPath = parentCommentPath.createChildCommentPath(descendantsTopPath);

		CommentV2 commentV2 = CommentV2.create(snowflake.nextId(), request.getContent(), request.getArticleId(),
			request.getWriterId()
			, childCommentPath);

		CommentV2 saved = commentV2Repository.save(commentV2);

		int result = articleCommentCountRepository.increase(request.getArticleId());
		if (result == 0) {
			ArticleCommentCount commentCount = ArticleCommentCount.init(request.getArticleId(), 1L);
			articleCommentCountRepository.save(commentCount);
		}

		return CommentResponse.from(saved);
	}

	private CommentV2 findParent(CommentCreateRequestV2 request) {
		String parentPath = request.getParentPath();
		if (parentPath == null) {
			return null;
		}

		return commentV2Repository.findByPath(parentPath)
			.filter(not(CommentV2::getDeleted))
			.orElseThrow();
	}

	private void delete(CommentV2 comment) {
		commentV2Repository.delete(comment);
		articleCommentCountRepository.decrease(comment.getArticleId());
		if (!comment.isRoot()) {
			commentV2Repository.findByPath(comment.getCommentPath().getParentPath())
				.filter(CommentV2::getDeleted)
				.filter(not(this::hasChildren))
				.ifPresent(this::delete);
		}
	}

	private boolean hasChildren(CommentV2 comment) {
		return commentV2Repository.findDescendantsTopPath(
			comment.getArticleId(),
			comment.getCommentPath().getPath()
		).isPresent();
	}

	public CommentResponse read(Long commentId) {
		return CommentResponse.from(
			commentV2Repository.findById(commentId).orElseThrow()
		);
	}

	@Transactional
	public void delete(Long commentId) {
		commentV2Repository.findById(commentId)
			.filter(not(CommentV2::getDeleted))
			.ifPresent(comment -> {
				if (hasChildren(comment)) {
					comment.delete();
				} else {
					delete(comment);
				}
			});
	}

	public CommentPageResponse readAll(Long articleId, Long page, Long pageSize) {
		long offset = (page - 1) * pageSize;
		List<CommentResponse> result = commentV2Repository.findAll(articleId, offset, pageSize).stream()
			.map(CommentResponse::from)
			.toList();

		Long pageLimit = PageLimitCalculator.calculatePageLimit(page, pageSize, 10L);
		Long limitedCount = commentV2Repository.count(articleId, pageLimit);

		return CommentPageResponse.of(result, limitedCount);
	}

	public List<CommentResponse> readAllInfiniteScroll(
		Long articleId,
		String lastPath,
		Long pageSize
	) {
		List<CommentV2> comments = lastPath == null ? commentV2Repository.findAllInfiniteScroll(articleId, pageSize)
			: commentV2Repository.findAllInfiniteScroll(articleId, lastPath, pageSize);

		return comments.stream()
			.map(CommentResponse::from)
			.toList();
	}

	public Long count(Long articleId) {
		return articleCommentCountRepository.findById(articleId)
			.map(ArticleCommentCount::getCommentCount)
			.orElse(0L);
	}
}
