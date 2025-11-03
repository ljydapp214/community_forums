package mtcc.board.articleread.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardArticleCountRepository {
	// article-read::board-article-count::board::{board_id}
	private static final String KEY_FORMAT = "article-read::board-article-count::board::%s";
	private final StringRedisTemplate redisTemplate;

	private String generateKey(Long boardId) {
		return KEY_FORMAT.formatted(boardId);
	}

	public void createOrUpdate(Long boardId, Long articleCount) {
		redisTemplate.opsForValue().set(generateKey(boardId), String.valueOf(articleCount));
	}

	public Long read(Long boardId) {
		String result = redisTemplate.opsForValue().get(generateKey(boardId));
		return result == null ? 0L : Long.valueOf(result);
	}
}
