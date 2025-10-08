package mtcc.board.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.websocket.server.PathParam;
import mtcc.board.comment.entity.CommentV2;

public interface CommentV2Repository extends JpaRepository<CommentV2, Long> {
	@Query("select c from CommentV2 c where c.commentPath.path = :commentPath")
	Optional<CommentV2> findByPath(@PathParam("commentPath") String commentPath);

	@Query(
		value = "select path from comment_v2"
			+ " where article_id = :articleId"
			+ " and path > :pathPrefix"
			+ " and path like concat(:pathPrefix, '%')"
			+ " order by path desc limit 1",
		nativeQuery = true
	)
	Optional<String> findDescendantsTopPath(
		@Param("articleId") Long articleId,
		@Param("pathPrefix") String pathPRefix
	);

	@Query(
		value = "select comment_v2.comment_id, comment_v2.content, comment_v2.article_id, "
			+ "comment_v2.writer_id, comment_v2.path, comment_v2.deleted, comment_v2.created_at"
			+ " from ("
			+ " select comment_id from comment_v2 where article_id = :articleId"
			+ " limit :limit offset :offset) t"
			+ " left join comment_v2 on t.comment_id = comment_v2.comment_id"
		, nativeQuery = true
	)
	List<CommentV2> findAll(
		@Param("articleId") Long articleId,
		@Param("offset") long offset,
		@Param("limit") long limit
	);

	@Query(
		value = "select count(*) from ("
			+ " select comment_id from comment_v2"
			+ " where article_id = :articleId limit :limit) t",
		nativeQuery = true
	)
	Long count(
		@Param("articleId") Long articleId,
		@Param("limit") long limit
	);

	@Query(
		value = "select comment_v2.comment_id, comment_v2.content, comment_v2.article_id, "
			+ "comment_v2.writer_id, comment_v2.path, comment_v2.deleted, comment_v2.created_at"
			+ " from comment_v2"
			+ " where article_id = :articleId"
			+ " order by path asc"
			+ " limit :limit"
		, nativeQuery = true
	)
	List<CommentV2> findAllInfiniteScroll(
		@Param("articleId") Long articleId,
		@Param("limit") Long limit
	);

	@Query(
		value = "select comment_v2.comment_id, comment_v2.content, comment_v2.article_id, "
			+ "comment_v2.writer_id, comment_v2.path, comment_v2.deleted, comment_v2.created_at"
			+ " from comment_v2"
			+ " where article_id = :articleId"
			+ " and path > :lastPath"
			+ " order by path asc"
			+ " limit :limit"
		, nativeQuery = true
	)
	List<CommentV2> findAllInfiniteScroll(
		@Param("articleId") Long articleId,
		@Param("lastPath") String lastPath,
		@Param("limit") Long limit
	);
}
