package mtcc.board.article.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountRepository {
	// view::article::{article_id}::view_count
	private static final String KEY_FORMAT = "view::article::%s::view_count";
	private final StringRedisTemplate redisTemplate;

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}

	public Long read(Long articleId) {
		String key = generateKey(articleId);
		String count = redisTemplate.opsForValue().get(key);
		return count == null ? 0L : Long.parseLong(count);
	}

	public Long increase(Long articleId) {
		String key = generateKey(articleId);
		return redisTemplate.opsForValue().increment(key);
	}
}
