package mtcc.board.article.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mtcc.board.article.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
	@Query(
		value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id,"
			+ "article.created_at, article.modified_at "
			+ "from ("
			+ " select article_id from article"
			+ " where board_id = :boardId "
			+ " order by article_id desc "
			+ " limit :limit offset :offset"
			+ " ) t left join article on t.article_id = article.article_id"
		, nativeQuery = true
	)
	List<Article> findAll(
		@Param("boardId") long boardId,
		@Param("offset") long offset,
		@Param("limit") long limit
	);

	@Query(
		value = "select count(*) from ("
			+ " select article.article_id from article where board_id = :boardId"
			+ " limit :limit"
			+ " ) t",
		nativeQuery = true
	)
	long countByBoardId(
		@Param("boardId") long boardId,
		@Param("limit") long limit
	);

	@Query(
		value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id,"
			+ "article.created_at, article.modified_at"
			+ " from article"
			+ " where article.board_id = :boardId"
			+ " order by article_id desc"
			+ " limit :limit",
		nativeQuery = true
	)
	List<Article> findAllInfiniteScroll(
		@Param("boardId") long boardId,
		@Param("limit") long limit
	);

	@Query(
		value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id,"
			+ "article.created_at, article.modified_at"
			+ " from article"
			+ " where article.board_id = :boardId"
			+ " and article.article_id < :lastArticleId"
			+ " order by article_id desc"
			+ " limit :limit",
		nativeQuery = true
	)
	List<Article> findAllInfiniteScroll(
		@Param("boardId") long boardId,
		@Param("lastArticleId") long lastArticleId,
		@Param("limit") long limit
	);
}
