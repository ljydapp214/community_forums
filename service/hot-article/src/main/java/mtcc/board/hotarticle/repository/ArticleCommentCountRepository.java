package mtcc.board.hotarticle.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleCommentCountRepository {
	private static final String KEY_FORMAT = "hot-article::article::%s::comment-count";
	private final StringRedisTemplate redisTemplate;

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}

	public void createOrUpdate(Long articleId, Long commentCount, Duration ttl) {
		redisTemplate.opsForValue().set(generateKey(articleId), commentCount.toString(), ttl);
	}

	public Long read(Long articleId) {
		String result = redisTemplate.opsForValue().get(generateKey(articleId));
		return result == null ? 0L : Long.parseLong(result);
	}
}
