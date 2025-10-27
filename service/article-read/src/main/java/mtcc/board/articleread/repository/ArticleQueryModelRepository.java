package mtcc.board.articleread.repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import mtcc.board.common.serializer.DataSerializer;

@Repository
@RequiredArgsConstructor
public class ArticleQueryModelRepository {
	// article-read::article::{article_id}
	private static final String KEY_FORMAT = "article-read::article::%s";
	private final StringRedisTemplate redisTemplate;
	private final JsonComponentModule jsonComponentModule;

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}

	private String generateKey(ArticleQueryModel model) {
		return generateKey(model.getArticleId());
	}

	public void create(ArticleQueryModel model, Duration ttl) {
		redisTemplate.opsForValue().set(generateKey(model), DataSerializer.serialize(model), ttl);
	}

	public void update(ArticleQueryModel model, Duration ttl) {
		redisTemplate.opsForValue().setIfPresent(generateKey(model), DataSerializer.serialize(model), ttl);
	}

	public void delete(Long articleId) {
		redisTemplate.delete(generateKey(articleId));
	}

	public Optional<ArticleQueryModel> read(Long articleId) {
		return Optional.ofNullable(redisTemplate.opsForValue()
				.get(generateKey(articleId)))
			.map(json ->
				DataSerializer.deserialize(json, ArticleQueryModel.class));
	}

	public Map<Long, ArticleQueryModel> readAll(List<Long> articleIds) {
		List<String> keyList = articleIds.stream().map(this::generateKey).toList();
		return redisTemplate.opsForValue().multiGet(keyList)
			.stream()
			.filter(Objects::nonNull)
			.map(json -> DataSerializer.deserialize(json, ArticleQueryModel.class))
			.collect(Collectors.toMap(ArticleQueryModel::getArticleId, Function.identity()));
	}
}
