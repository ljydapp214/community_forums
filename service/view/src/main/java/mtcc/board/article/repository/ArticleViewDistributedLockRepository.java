package mtcc.board.article.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleViewDistributedLockRepository {
	// view::article::{article_id}::user::{user_id}::lock
	private static final String KEY_FORMAT = "view::article::%s::user::%s::lock";
	private final StringRedisTemplate redisTemplate;

	private String generateKey(Long articleId, Long userId) {
		return KEY_FORMAT.formatted(articleId, userId);
	}
	
	public boolean lock(Long articleId, Long userId, Duration ttl) {
		String key = generateKey(articleId, userId);
		Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", ttl);
		if (success == null) {
			return false;
		}
		return success;
	}
}
