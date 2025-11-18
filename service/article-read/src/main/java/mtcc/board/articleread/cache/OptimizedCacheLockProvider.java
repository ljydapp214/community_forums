package mtcc.board.articleread.cache;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OptimizedCacheLockProvider {
	private static final String KEY_FORMAT = "optimized-cache-lcok::%s";
	private static final Duration LOCK_TTL = Duration.ofSeconds(3);

	private final StringRedisTemplate redisTemplate;

	private String generateKey(String key) {
		return KEY_FORMAT.formatted(key);
	}

	public boolean lock(String key) {
		return redisTemplate.opsForValue()
			.setIfAbsent(generateKey(key), "1", LOCK_TTL);
	}

	public void unlock(String key) {
		redisTemplate.delete(generateKey(key));
	}
}
