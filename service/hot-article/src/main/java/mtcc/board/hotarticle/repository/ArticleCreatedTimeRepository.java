package mtcc.board.hotarticle.repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleCreatedTimeRepository {
	// 좋아요 이벤트가 왔는데, 이 이벤트에 대하 ㄴ게시글이 오늘 게시글인지 확인이 필요
	// 자체적으로 게시글 생성 시간을 저장하고 있으면 게시글 서비스에 질의하는 과정을 생략해도 됨
	// "hot-article::article::{article_id}::created-time"
	private static final String KEY_FORMAT = "hot-article::article::%s::created-time";
	private final StringRedisTemplate redisTemplate;

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}

	public void createOrUpdate(Long articleId, LocalDateTime createdTime, Duration ttl) {
		redisTemplate.opsForValue().set(
			generateKey(articleId),
			String.valueOf(createdTime.toInstant(ZoneOffset.UTC).toEpochMilli()),
			ttl
		);
	}

	public void delete(Long articleId) {
		redisTemplate.delete(generateKey(articleId));
	}

	public LocalDateTime read(Long articleId) {
		String result = redisTemplate.opsForValue().get(generateKey(articleId));
		if (result == null) {
			return null;
		}
		Long epochMilli = Long.parseLong(result);
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC);
	}
}
