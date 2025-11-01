package mtcc.board.hotarticle.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountRepository {
	private static final String KEY_FORMAT = "hot-article::article::%s::view-count";
	private final StringRedisTemplate redisTemplate;

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}

	public void createOrUpdate(Long articleId, Long viewCount, Duration ttl) {
		redisTemplate.opsForValue().set(generateKey(articleId), viewCount.toString(), ttl);
	}

	public Long read(Long articleId) {
		String result = redisTemplate.opsForValue().get(generateKey(articleId));
		return result == null ? 0L : Long.parseLong(result);
	}

}
