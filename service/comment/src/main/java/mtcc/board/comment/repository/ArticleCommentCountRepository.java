package mtcc.board.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import mtcc.board.comment.entity.ArticleCommentCount;

public interface ArticleCommentCountRepository extends JpaRepository<ArticleCommentCount, Long> {
	@Query(
		value = "update article_comment_count set comment_count = comment_count + 1 where article_id = :articleId",
		nativeQuery = true
	)
	@Modifying
	int increase(Long articleId);

	@Query(
		value = "update article_comment_count set comment_count = comment_count -1 where article_id = :articleId",
		nativeQuery = true
	)
	@Modifying
	int decrease(Long articleId);
}
