package mtcc.board.articleread.repository;

import java.util.List;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleIdListRepository {
	// article-read::board::{board_id}::article-list
	private static final String KEY_FORMAT = "article-read::board::%s::article-list";
	private final StringRedisTemplate redisTemplate;

	// zset 에 저장되는 articleId 는 19자리로 패딩처리
	private String toPaddedString(Long articleId) {
		return "%019d".formatted(articleId);
	}

	private String generateKey(Long boardId) {
		return KEY_FORMAT.formatted(boardId);
	}

	public void add(Long boardId, Long articleId, Long limit) {
		redisTemplate.executePipelined((RedisCallback<?>)action -> {
			StringRedisConnection conn = (StringRedisConnection)action;
			String key = generateKey(boardId);
			conn.zAdd(key, 0, toPaddedString(articleId));
			// 0 부터 뒤에서 limit + 1 까지 삭제
			conn.zRemRange(key, 0, -limit - 1);
			return null;
		});
	}

	public void delete(Long boarId, Long articleId) {
		redisTemplate.opsForZSet().remove(generateKey(boarId), toPaddedString(articleId));
	}

	public List<Long> readAll(Long boardId, Long offset, Long limit) {
		return redisTemplate.opsForZSet()
			.reverseRange(generateKey(boardId), offset, offset + limit - 1)
			.stream()
			.map(Long::valueOf)
			.toList();
	}

	public List<Long> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long limit) {
		Range<String> range = lastArticleId == null ? Range.unbounded()
			: Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId)));
		Limit limitCount = Limit.limit().count(limit.intValue());

		return redisTemplate.opsForZSet()
			.reverseRangeByLex(generateKey(boardId), range, limitCount).stream()
			.map(Long::valueOf)
			.toList();
	}
}
